<%@ page import="com.progetto.sitoforzearmate.model.mo.Base.Base" %>
<%@ page import="java.util.List" %><%--
  Created by IntelliJ IDEA.
  User: stef
  Date: 15/07/2023
  Time: 12:52
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Mappa basi aeree italiane</title>
    <link rel="stylesheet" href="https://openlayers.org/en/v6.13.0/css/ol.css" type="text/css">
    <style>
        #map {
            position: relative;
            width: 100%;
            height: 500px;
        }
        #info {
            position: absolute;
            top: 10px;
            left: 10px;
            background-color: white;
            padding: 5px;
            z-index: 100;
            white-space: pre-wrap;
        }
    </style>
    <script src="https://openlayers.org/en/v6.13.0/build/ol.js"></script>
</head>
<body>

<% List<Base> baseList = (List<Base>) request.getAttribute("Basi");%>

<div id="map">
    <div id="info"></div>
</div>

<script>
    var map = new ol.Map({
        target: 'map',
        layers: [
            new ol.layer.Tile({
                source: new ol.source.OSM()     // mappa di base di openalayer
            })
        ],
        view: new ol.View({
            center: ol.proj.fromLonLat([12.5674, 41.8719]), // Coordinate di Roma
            zoom: 6
        })
    });

    var markerLayer = new ol.layer.Vector({
        source: new ol.source.Vector()
    });
    map.addLayer(markerLayer);

    var pointerMoveInteraction = new ol.interaction.Pointer({
        handleMoveEvent: function(event) {
            var coordinate = event.coordinate;
            var pixel = event.pixel;
            var hit = map.forEachFeatureAtPixel(pixel, function(feature) {
                return feature;
            });
            if (hit) {
                var infoElement = document.getElementById('info');
                var name = hit.get('name');
                infoElement.innerHTML = name;
                infoElement.style.display = 'block';
                infoElement.style.left = pixel[0] + 10 +'px';
                infoElement.style.top = (pixel[1] - 15) + 'px';
            } else {
                document.getElementById('info').style.display = 'none';
            }
        }
    });
    map.addInteraction(pointerMoveInteraction);

    var marker = []
    <%for(int i=0; i<baseList.size(); i++){%>
        marker.push(
        new ol.Feature({
            geometry: new ol.geom.Point(ol.proj.fromLonLat([<%=baseList.get(i).getLongitudine()%>,<%=baseList.get(i).getLatitudine()%>])),
            name: 'Base aerea di <%=baseList.get(i).getLocazione()%>\n<%=baseList.get(i).getVia()%>, <%=baseList.get(i).getCAP()%>, <%=baseList.get(i).getProvincia()%>'
        }))
    <%}%>
    
    for(let i=0; i<marker.length; i++) markerLayer.getSource().addFeature(marker[i]);
</script>
</body>
</html>
