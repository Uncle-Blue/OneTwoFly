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

    public function weatherCondition($condition=''){

        $table = [
            'MI' => '薄霧',
            'PR' => '部分（霧）',
            'BC' => 'Patches（霧）',
            'DR' => '低吹',
            'BL' => '吹',
            'SH' => '陣性',
            'TS' => '雷暴',
            'FZ' => '凍',
            'DZ' => '毛雨',
            'RA' => '雨',
            'SN' => '雪',
            'SG' => '雪粒',
            'IC' => '冰晶',
            'PL' => '霙',
            'GR' => '雹',
            'GS' => '霰',
            'UP' => '不明降水',
            'BR' => '靄',
            'FG' => '霧',
            'FU' => '煙',
            'VA' => '火山灰',
            'DU' => '沙塵暴',
            'SA' => '沙',
            'HZ' => '霾',
            'PY' => '海沫、水沫',
            'SQ' => '颮',
            'FC' => '龍捲風',
            'SS' => '沙暴',
            'DS' => '塵暴',
        ];

        $describtion = "";

        try{
            $condition_array = explode(' ', $condition);
            if( count($condition_array)>0 ){
                foreach ($condition_array as $each) {
                    if(array_key_exists($each, $table)){
                        $describtion .= $table[$each]." ";
                    }
                }
                return $describtion;
            }else{
                return '';
            }
        }catch(Exception $e){
            return '';
        }


    }

    public function flight(Request $request)
    {

        $options = array(
                             'trace' => true,
                             'exceptions' => 0,
                             'login' => 'sapc87952',
                             'password' => env('flightaware_key', ''),
                        );
        $client = new \SoapClient('http://flightxml.flightaware.com/soap/FlightXML2/wsdl', $options);

        // get flight number

        $params = array("ident" => $request->input('flight'), "howMany" => "1");
        $result2ports = $client->FlightInfo($params);

        //起點機場代號
        $origin = $result2ports->FlightInfoResult->flights->origin;
        //終點機場代號
        $destination = $result2ports->FlightInfoResult->flights->destination;

        $params_ori = array("airportCode" => $origin);
        $params_des = array("airportCode" => $destination);

        $result_ori = $client->AirportInfo($params_ori);
        $result_des = $client->AirportInfo($params_des);

        //起點經緯度*
        $origin_geo = json_decode(json_encode($result_ori));
        //終點經緯度*
        $destination_geo = json_encode($result_des);

        //起點氣象
        $params_ori_weather = array("airport" => $origin, "startTime" => 0, "howMany" => 1, "offset" => 0);
        $weather_ori = $client->MetarEx($params_ori_weather);

        //起點氣象報告*
        $weather_report_ori = json_encode($weather_ori);

        //終點氣象
        $params_des_weather = array("airport" => $destination, "startTime" => 0, "howMany" => 1, "offset" => 0);
        $weather_des = $client->MetarEx($params_des_weather);

        //終點氣象報告*
        $weather_report_des = json_decode(json_encode($weather_des));

        try{
            $result = array(
                  "airportName" => $destination,
                  "latitude" => $origin_geo->AirportInfoResult->latitude,
                  "longitude" => $origin_geo->AirportInfoResult->longitude,
                  "visibility" => $weather_report_des->MetarExResult->metar->visibility,
                  "humidity" => $weather_report_des->MetarExResult->metar->temp_relhum,
                  "temperature" => $weather_report_des->MetarExResult->metar->temp_air,
                  "weatherProfile" => $this->weatherCondition($weather_report_des->MetarExResult->metar->conditions),
            );
        }catch(Exception $e){

        }

        print_r(json_encode($result));
        return;
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
