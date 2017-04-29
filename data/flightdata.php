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
        
        //起點機場代號
        $origin = $result2ports->FlightInfoResult->flights->origin;
        //終點機場代號
        $destination = $result2ports->FlightInfoResult->flights->destination;

        print_r($result2ports);

        
        $params_ori = array("airportCode" => "$origin");

        
        $params_des = array("airportCode" => "$destination");

        $result_ori = $client->AirportInfo($params_ori);
        $result_des = $client->AirportInfo($params_des);

       
        

        //起點經緯度*
        $origin_geo = json_encode(result_ori);
        print(origin_geo);
        //終點經緯度*
        $destination_geo = json_encode(result_des);
        print(destination_geo);

        //起點氣象
        $params_ori_weather = array("airportCode" => $origin, "startTime" => 0, "howMany" => 1, "offset" => 0)
        $weather_ori = $client->MetarEx($params_ori_weather);
        
        //起點氣象報告*
        $weather_report_ori = json_encode($weather_ori);

        //終點氣象
        $params_des_weather = array("airportCode" => $destination, "startTime" => 0, "howMany" => 1, "offset" => 0)
        $weather_des = $client->MetarEx($params_ori_weather);
        
        //終點氣象報告*
        $weather_report_des = json_encode($weather_des);



?>