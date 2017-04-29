<?php
$options = array(
                     'trace' => true,
                     'exceptions' => 0,
                     'login' => 'sapc87952',
                     'password' => 'ad6edc63c0df3606653774e2628f9b81562931da',
                );
        $client = new \SoapClient('http://flightxml.flightaware.com/soap/FlightXML2/wsdl', $options);

        // get flight number

        $flightnum = json_decode($json);

        $params = array("ident" => "flightnum", "howMany" => "1");
        $result2ports = $client->FlightInfo($params);
        
        $origin = $result2ports->FlightInfoResult->flights->origin;
        $destination = $result2ports->FlightInfoResult->flights->destination;

        print_r($result2ports);


        $params_ori = array("airportCode" => "$origin");
        $params_des = array("airportCode" => "$destination");

        $result_ori = $client->AirportInfo($params_ori);
        $result_des = $client->AirportInfo($params_des);

        print_r($result_ori);
        print_r($result_des);

        $ori_lon = $result_ori->AirportInfoResult->longitude;
        $ori_lat = $result_ori->AirportInfoResult->latitude;

        $des_lon = $result_des->AirportInfoResult->longitude;
        $des_lat = $result_des->AirportInfoResult->latitude;



?>