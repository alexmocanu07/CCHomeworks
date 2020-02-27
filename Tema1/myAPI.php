<?php

    class myAPI
    {
        private $GGL_API_KEY = "";
        private $city;
        private $radius = 2000;
        private $cityLat;
        private $cityLng;
        private $POITypes = Array("restaurant", "bar", "bank", "atm", "beauty_salon", "bus_station", "cafe", "car_wash", "church", "gym", "school");
        private $POIType;
        private $places = Array();
        private $placesWithAddress = Array();
        function getCityCoord(){
            $date = date('m/d/Y h:i:s a', time());
            $startTime = time();


            if (isset($_POST["city"]))
                $this->city = $_POST["city"];
            else
                $this->city = "Iasi";
            $GeocodingURL = "https://maps.googleapis.com/maps/api/geocode/json?address=" . $this->city . "&key=" . $this->GGL_API_KEY;

            $resultJson = file_get_contents($GeocodingURL);
            $result = json_decode($resultJson);
            $status = $result->status;
            $this->cityLat = $result->results[0]->geometry->location->lat;
            $this->cityLng = $result->results[0]->geometry->location->lng;

            $endTime = time();
            $latency = $endTime - $startTime;
            $log = "[". $date . "]: API =" . "Geocoding" .
                " arg= " . $this->city .
                " rasp= " . $status .
                " latency= " . $latency . "\r\n";
            file_put_contents('./log_'.date("j.n.Y").'.txt', $log, FILE_APPEND);
        }

        function getPOIType(){
            $date = date('m/d/Y h:i:s a', time());
            $startTime = time();

            $RandomURL = "https://csrng.net/csrng/csrng.php?min=0&max=" . (sizeof($this->POITypes) - 1);


            $randomJson = file_get_contents($RandomURL);
            $randomDecode = json_decode($randomJson);
            $index = $randomDecode[0]->random;
            $status = $randomDecode[0]->status;
            $this->POIType = $this->POITypes[$index];

            $endTime = time();
            $latency = $endTime - $startTime;
            $log = "[". $date . "]: API =" . "RNG" .
                " arg= " . (sizeof($this->POITypes) - 1) .
                " rasp= " . $status .
                " latency= " . $latency . "\r\n";
            file_put_contents('./log_'.date("j.n.Y").'.txt', $log, FILE_APPEND);
        }

        function getPlaces(){
            $dateALL = date('m/d/Y h:i:s a', time());
            $startTimeALL = time();
            $pageToken = null;
            do {
                $date = date('m/d/Y h:i:s a', time());
                $startTime = time();
                $NearbyURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" .
                    $this->cityLat . "," . $this->cityLng .
                    "&radius=" . $this->radius .
                    "&type=" . $this->POIType .
                    ($pageToken == null ? "" : "&pagetoken=" . $pageToken) .
                    "&key=" . $this->GGL_API_KEY;

                $nearbyResultJson = file_get_contents($NearbyURL);
                $nearbyResult = json_decode($nearbyResultJson);
                $status = $nearbyResult->status;

                try {
                    $pageToken = $nearbyResult->next_page_token;
                } catch
                (Exception $e) {
                    $pageToken = null;
                }
                foreach ($nearbyResult->results as $result) {
                    $current = Array();
                    $current["name"] = $result->name;
                    $current["lat"] = $result->geometry->location->lat;
                    $current["lng"] = $result->geometry->location->lng;

                    array_push($this->places, $current);
                }
                sleep(2);
                $endTime = time();
                $latency = $endTime - $startTime;
                $log = "[". $date . "]: API =" . "NearbySearch" .
                    " arg= " . $this->cityLat . "," . $this->cityLng . "," .
                    $this->radius . "," .
                    $this->POIType .
                    " rasp= " . $status .
                    " latency= " . $latency . "\r\n";
                file_put_contents('./log_'.date("j.n.Y").'.txt', $log, FILE_APPEND);
            } while ($pageToken);
            $endTimeALL = time();
            $latencyALL = $endTimeALL - $startTimeALL;
            $statusALL = "done";
            $log = "[". $dateALL . "]: API =" . "NearbySearchALL" .  " rasp= " . $statusALL .
                " latency= " . $latencyALL . "\r\n";
            file_put_contents('./log_'.date("j.n.Y").'.txt', $log, FILE_APPEND);
        }

        function getPlacesAddresses(){
            $startTimeALL = time();
            $dateALL = date('m/d/Y h:i:s a', time());
            foreach ($this->places as $place) {
                $date = date('m/d/Y h:i:s a', time());
                $startTime = time();
                $lat = $place["lat"];
                $lng = $place["lng"];

                $ReverseGeocodingURL = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" . $lat . "," . $lng .
                    "&key=" . $this->GGL_API_KEY;
                $addressJson = file_get_contents($ReverseGeocodingURL);
                $addressResult = json_decode($addressJson);
                $status = $addressResult->status;
                $address = $addressResult->results[0]->formatted_address;
                $newPlace["name"] = $place["name"];
                $newPlace["address"] = $address;
                array_push($this->placesWithAddress, $newPlace);
                ?>
                <p> <?php echo $place["name"] . ", " . $address; ?></p>
                <?php
                $endTime = time();
                $latency = $endTime - $startTime;
                $log = "[". $date . "]: API =" . "ReverseGeocoding" .
                    " arg= " . $lat . "," . $lng . "," .
                    " rasp= " . $status .
                    " latency= " . $latency . "\r\n";
                file_put_contents('./log_'.date("j.n.Y").'.txt', $log, FILE_APPEND);
            }
            $endTimeALL = time();
            $latencyALL = $endTimeALL - $startTimeALL;
            $statusALL = "done";
            $log = "[". $dateALL . "]: API =" . "ReverseGeocodingALL" .
                " rasp= " . $statusALL .
                " latency= " . $latencyALL . "\r\n";
            file_put_contents('./log_'.date("j.n.Y").'.txt', $log, FILE_APPEND);
        }
        function call()
        {
            $fn = fopen("API_KEY.txt.txt", "r");
            $this->GGL_API_KEY = fgets($fn);

            fclose($fn);
            $startTime = time();
            $date = date('m/d/Y h:i:s a', time());
            $this->getCityCoord();
            $this->getPOIType();


            ?>
            <p>Categoria din care vor face parte punctele de interes este <strong><?php echo $this->POIType ?> </strong></p>
            <br>
            <?php

            $this->getPlaces();

            $this->getPlacesAddresses();

            $endTime = time();
            $latency = $endTime - $startTime;
            $status = "done";
            $log = "[". $date . "]: API =" . "myAPI" .
                " rasp= " . $status .
                " latency= " . $latency . "\r\n";
            file_put_contents('./log_'.date("j.n.Y").'.txt', $log, FILE_APPEND);

        }
    }

    $myAPiObj = new myAPI();
    $myAPiObj->call();




