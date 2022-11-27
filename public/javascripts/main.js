$(document).ready(() => {
    initMap();
});

let map;
let userLocation;
let sightingMarkers = [];
let radiusCircle = null;

class UserLocation {
     constructor(lat, lon, radius) {
         this.latitude = lat;
         this.longitude = lon;
         this.radius = radius;
     }
     toCookie() {
         return this.latitude.toString() + "|" + this.longitude.toString();
     }
     static fromCookie(cookieString) {
         const parts = cookieString.split("|");
         const lat = parseFloat(parts[0]);
         const lon = parseFloat(parts[1]);
         if (!isNaN(lat) && !isNaN(lon)) {
             return new UserLocation(lat, lon, 10_000);
         }
         return null;
    }
    toRequest() {
        return new URLSearchParams({
            latitude: this.latitude,
            longitude: this.longitude,
            radius: this.radius,
        });
    }
}

function initMap() {
    map = L.map("map");
    L.tileLayer("https://tile.openstreetmap.org/{z}/{x}/{y}.png", {
        maxZoom: 19,
        attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
    }).addTo(map);

    if (!loadLastLocation()) {
        map.fitWorld();
    }

    $("#locate-btn").click(locateMe);
    $("#add-btn").click(createSighting);
    $("#radius-slider").on("input change", changeRadius);
}

function loadLastLocation() {
    const lastLocation = UserLocation.fromCookie(getCookie("last-location"));
    if (lastLocation !== null) {
        map.setView([lastLocation.latitude, lastLocation.longitude], 16);
        userLocation = lastLocation;
        showRadiusCircle();
        loadNearSightings();
        return true;
    }
    return false;
}

function locateMe() {
    map.locate({ setView: true, maxZoom: 16 });

    map.on("locationfound", (event) => {
        const radius = event.accuracy;
        userLocation = new UserLocation(event.latlng.lat, event.latlng.lng, 10_000);

        const location = new L.marker(event.latlng).addTo(map);
        location._icon.classList.add("red-marker");
        location.bindPopup("You are within " + radius + " meters from this point").openPopup();

        // Remember this location next time
        setCookie("last-location", "", 0);
        setCookie("last-location", userLocation.toCookie(), 24*3600);

        showRadiusCircle();
        loadNearSightings();

        setTimeout(() => {
            map.removeLayer(location);
        }, 5000);
    });

    map.on("locationerror", (event) => {
        alert(event.message);
    });
}

function createSighting() {
    alert("Click anywhere on the map to place your sighting");
    $(".leaflet-container").css("cursor", "pointer");
    map.on("click", (event) => {
        map.off("click");
        const temporarySighting = new L.marker(event.latlng, { draggable: true }).addTo(map);
        temporarySighting._icon.classList.add("green-marker");
        temporarySighting.on("dblclick", () => {
            temporarySighting.off("dblclick");
            temporarySighting.dragging.disable();
            console.log(temporarySighting.getLatLng().lat);

            fetch(document.location.origin + "/sighting", {
                method: "POST",
                credentials: "include",
                headers: {
                    "Csrf-Token": getCookie("csrf-token"),
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    latitude: temporarySighting.getLatLng().lat,
                    longitude: temporarySighting.getLatLng().lng,
                }),
            })
                .then(response => response.json())
                .then(data => {
                    console.log(data);
                    loadNearSightings();
                });

            map.removeLayer(temporarySighting);
        });

        temporarySighting.addTo(map);
        $(".leaflet-container").css("cursor", "");
    });
}

function loadNearSightings() {
    sightingMarkers.forEach(sightingMarker => map.removeLayer(sightingMarker));
    sightingMarkers = [];

    fetch(document.location.origin + "/sightings?" + userLocation.toRequest())
        .then(response => response.json())
        .then(sightings => {
            sightings.forEach(sighting => {
                let sightingMarker = new L.marker([
                    sighting.latitude,
                    sighting.longitude
                ]).addTo(map);
                //sightingMarker._icon.classList.add("yellow-marker");
                sightingMarkers.push(sightingMarker);
            });
        });
}

function changeRadius() {
    const values = [100, 200, 300, 500, 1000, 2000, 3000, 5000, 10000, 30000, 50000, 70000, 100000];
    $("#radius-div p")[0].innerText = values[$(this).val()];
    userLocation.radius = values[$(this).val()];

    showRadiusCircle();
    loadNearSightings();
}

function showRadiusCircle() {
    if (radiusCircle !== null) {
        map.removeLayer(radiusCircle);
        radiusCircle = null;
    }
    const circle = new L.circle([userLocation.latitude, userLocation.longitude], userLocation.radius, {
        className: "smooth-hide"
    }).addTo(map);
    radiusCircle = circle;

    setTimeout(() => {
        $(".smooth-hide").animate({ opacity: 0 }, 1000, () => {
            if (circle === radiusCircle) {
                map.removeLayer(radiusCircle);
                radiusCircle = null;
            }
        });
    }, 1000);
}