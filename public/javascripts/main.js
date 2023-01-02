$(document).ready(() => {
    initMap();

    $("#si-btn").click(changeSightingInformation);
    $("#srm-btn").click(deleteSighting);
});

let map;
let userLocation;
let sightings = [];
let radiusCircle = null;
let selectedSighting = null;

const RADIUS = [100, 200, 300, 500, 1000, 2000, 3000, 5000, 10000, 30000, 50000, 70000, 100000];

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
    $("#change-location-btn").click(changeLocation);
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
    sightings.forEach(marker => map.removeLayer(marker));
    sightings = [];

    fetch(document.location.origin + "/sightings?" + userLocation.toRequest())
        .then(response => response.json())
        .then(nearSightings => {
            nearSightings.forEach(sighting => {
                const marker = new L.marker([
                    sighting.latitude,
                    sighting.longitude
                ]).addTo(map);
                if (sighting.creator === identity) {
                    marker._icon.classList.add("purple-marker");
                }
                marker.sighting = sighting;
                marker.on("click", showSightingInfo);
                sightings.push(marker);
            });
        });
}

function changeRadius() {
    $("#radius-div p")[0].innerText = RADIUS[$(this).val()];
    userLocation.radius = RADIUS[$(this).val()];

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
        $(".smooth-hide").fadeOut(1000, () => {
            if (circle === radiusCircle) {
                map.removeLayer(radiusCircle);
                radiusCircle = null;
            }
        });
    }, 1000);
}

function showSightingInfo(event) {
    const sighting = event.target.sighting;

    disableSIEdit();

    if ($("#sighting-info").is(":hidden")) {
        $("#sighting-info").fadeIn();
    }

    if (selectedSighting === null || sighting.id !== selectedSighting.id) {
        const fields = $("#sighting-info p");
        fields[0].innerText = convertDMS(sighting.latitude, sighting.longitude);
        fields[1].innerText = "Created by: " + sighting.creator;
        fields[2].innerText = "On: " + new Date(sighting.timestamp);
        $("#si-form textarea")[0].value = sighting.information;
        selectedSighting = sighting;
    } else {
        $("#sighting-info").fadeOut();
        selectedSighting = null;
    }
}

function changeLocation() {
    alert("Click anywhere on the map to place your location");
    $(".leaflet-container").css("cursor", "pointer");
    map.on("click", (event) => {
        map.off("click");
        const locationMarker = new L.marker(event.latlng, { draggable: true }).addTo(map);
        locationMarker._icon.classList.add("red-marker");
        locationMarker.on("dblclick",() => {
            locationMarker.off("dblclick");
            locationMarker.dragging.disable();

            const lat = locationMarker.getLatLng().lat;
            const lng = locationMarker.getLatLng().lng;
            if (userLocation) {
                userLocation.latitude = lat;
                userLocation.longitude = lng;
            } else {
                userLocation = new UserLocation(lat, lng, 10_000);
            }

            // Remember this location next time
            setCookie("last-location", "", 0);
            setCookie("last-location", userLocation.toCookie(), 24*3600);

            showRadiusCircle();
            loadNearSightings();

            map.removeLayer(locationMarker);
        });

        locationMarker.addTo(map);
        $(".leaflet-container").css("cursor", "");
    });
}

function changeSightingInformation() {
    const textarea = $("#si-form textarea")[0];

    if (textarea.disabled && identity === selectedSighting.creator) {
        enableSIEdit();
    } else {
        disableSIEdit();

        // Update the sighting information
        const information = textarea.value;
        fetch(document.location.origin + "/update_sighting", {
            method: "PUT",
            credentials: "include",
            headers: {
                "Csrf-Token": getCookie("csrf-token"),
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                sightingId: selectedSighting.id,
                information: information,
            }),
        })
            .then(response => response.json())
            .then(jsonResponse => {
                if (jsonResponse.error !== undefined) {
                    console.log(jsonResponse.error);
                } else {
                    selectedSighting.information = jsonResponse.information;
                }
            });
    }
}

function disableSIEdit() {
    $("#si-form textarea")[0].disabled = true;
    const button = $("#si-btn")[0];
    console.log(button.classList);
    button.classList.remove("mdi-content-save");
    button.classList.add("mdi-pencil");
}

function enableSIEdit() {
    $("#si-form textarea")[0].disabled = false;
    const button = $("#si-btn")[0];
    button.classList.remove("mdi-pencil");
    button.classList.add("mdi-content-save");
}

function deleteSighting() {
    fetch(document.location.origin + "/delete_sighting/" + selectedSighting.id, {
        method: "DELETE",
        credentials: "include",
        headers: {
            "Csrf-Token": getCookie("csrf-token"),
            "Content-Type": "application/json",
        },
    })
        .then(response => response.json())
        .then(jsonResponse => {
            console.log(jsonResponse);
            if (jsonResponse.id !== undefined) {
                $("#sighting-info").fadeOut();
                selectedSighting = null;
                loadNearSightings();
            } else {
                console.log(jsonResponse.error);
            }
        });
}