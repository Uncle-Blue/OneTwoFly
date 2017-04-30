<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;

class crawlController extends Controller
{
    //
    public function index()
    {
        return '111111';
    }

    public function flight(Request $request)
    {

        $options = array(
                             'trace' => true,
                             'exceptions' => 0,
                             'login' => 'sapc87952',
                             'password' => 'ad6edc63c0df3606653774e2628f9b81562931da',
                        );
                $client = new \SoapClient('http://flightxml.flightaware.com/soap/FlightXML2/wsdl', $options);

                // get flight number

                $params = array("ident" => $request->input('flight'), "howMany" => "1");
                $result2ports = $client->FlightInfo($params);

                //起點機場代號
                $origin = $result2ports->FlightInfoResult->flights->origin;
                //終點機場代號
                $destination = $result2ports->FlightInfoResult->flights->destination;

                print_r($result2ports);

                $params_ori = array("airport" => "$origin");

                $params_des = array("airport" => "$destination");

                $result_ori = $client->AirportInfo($params_ori);
                $result_des = $client->AirportInfo($params_des);

                //起點經緯度*
                $origin_geo = json_encode($result_ori);
                //print(origin_geo);
                //終點經緯度*
                $destination_geo = json_encode($result_des);
                //print(destination_geo);

                //起點氣象
                $params_ori_weather = array("airportCode" => $origin, "startTime" => 0, "howMany" => 1, "offset" => 0);
                $weather_ori = $client->MetarEx($params_ori_weather);

                //起點氣象報告*
                $weather_report_ori = json_encode($weather_ori);

                //終點氣象
                $params_des_weather = array("airportCode" => $destination, "startTime" => 0, "howMany" => 1, "offset" => 0);
                $weather_des = $client->MetarEx($params_des_weather);

                //終點氣象報告*
                $weather_report_des = json_encode($weather_des);

                $result = array(
                    //dest airport
                      "airportName" => $destination,
                      "latitude" => $result_ori[1],
                      "longitude" => $result_ori[0],
                      "visibility" => $weather_des['visibility'],
                      "humidity" => $weather_des['temp_relhum'],
                      "temperature" => $weather_des['temp_air'],
                      "weatherProfile" => $weather_des['conditions']
                );

        //var_dump($request->input('flight'));
        return json_encode($result);
    }

    public function weather(Request $request){
        var_dump($request->input('weather'));
        return ;
    }

    public function create()
    {
        return "successssss";
    }

    /**
     * Store a newly created resource in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @return \Illuminate\Http\Response
     */
    public function store(Request $request)
    {
        return "successssss";
    }

    /**
     * Display the specified resource.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function show($id)
    {
        return "successssss";
    }

    /**
     * Show the form for editing the specified resource.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function edit($id)
    {
        return "successssss";
    }

    /**
     * Update the specified resource in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function update(Request $request, $id)
    {
        return "successssss";
    }

    /**
     * Remove the specified resource from storage.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function destroy($id)
    {
        return "successssss";
    }
}
