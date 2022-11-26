$(document).ready(() => {
    initMap();
});

let map;
let userLocation;

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
        return {
            latitude: this.latitude,
            longitude: this.longitude,
            radius: this.radius,
        };
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
    $("#add-btn").click(addSighting);
}

function loadLastLocation() {
    const lastLocation = UserLocation.fromCookie(getCookie("last-location"));
    if (lastLocation !== null) {
        map.setView([lastLocation.latitude, lastLocation.longitude], 16);
        userLocation = lastLocation;
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
        location._icon.classList.add("rotate-hue");
        location.bindPopup("You are within " + radius + " meters from this point").openPopup();

        // Remember this location next time
        setCookie("last-location", "", 0);
        setCookie("last-location", userLocation.toCookie(), 24*3600);

        setTimeout(() => {
            map.removeLayer(location);
        }, 5000);
    });

    map.on("locationerror", (event) => {
        alert(event.message);
    });
}

function addSighting() {
    alert("Click anywhere on the map to place your sighting");
    $(".leaflet-container").css("cursor", "pointer");
    map.on("click", (event) => {
        map.off("click");
        const temporarySighting = new L.marker(event.latlng, { draggable: true }).addTo(map);
        temporarySighting._icon.classList.add("green-marker");
        temporarySighting.on("dblclick", () => {
            temporarySighting.off("dblclick");
            temporarySighting.dragging.disable();
            map.removeLayer(temporarySighting);
            alert("TODO: send sighting to backend");
        });
        temporarySighting.addTo(map);
        $(".leaflet-container").css("cursor", "");
    });
}