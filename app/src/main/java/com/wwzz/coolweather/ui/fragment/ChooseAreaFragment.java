package com.wwzz.coolweather.ui.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wwzz.coolweather.R;
import com.wwzz.coolweather.model.City;
import com.wwzz.coolweather.model.County;
import com.wwzz.coolweather.model.Province;
import com.wwzz.coolweather.ui.activity.MainActivity;
import com.wwzz.coolweather.ui.activity.WeatherActivity;
import com.wwzz.coolweather.util.HttpUtil;
import com.wwzz.coolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 作者：wz created on 2017/3/6 13:48
 * 邮箱：wangzhao9211@163.com
 * 功能：
 */

public class ChooseAreaFragment extends Fragment {
    TextView textTitle ;
    Button buttonBack;
    ListView listViewCity;
    ArrayAdapter<String> adapter;

    private List<String> dataList=new ArrayList<>();

    /*
    * 省列表数据
    * */
    private List<Province> provinceList;
    /*
    * 市列表数据
    * */
    private List<City> cityList;
    /*
    * 县列表数据
    * */
    private List<County> countyList;

    /*
    * 选中的省份
    * */
    private Province selectedProvince;

    /*
    * 选中的城市
    * */
    private City selectedCity;

    public static final int LEVEL_PROVINCE=0;
    public static final int LEVEL_CITY=1;
    public static final int LEVEL_COUNTY=2;

    /*
    * 当前选中的级别
    * */
    private int currentLevel;

    private ProgressDialog progressDialog;//加载进度显示



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.choose_area,container,false);
        textTitle= (TextView) view.findViewById(R.id.titleText);
        buttonBack= (Button) view.findViewById(R.id.back_button);
        listViewCity= (ListView) view.findViewById(R.id.list_view);
        adapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_expandable_list_item_1,dataList);
        listViewCity.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        queryProvinces();//第一次先加载省数据

        listViewCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                根据当前等级加载数据
                if(currentLevel==LEVEL_PROVINCE){
                    selectedProvince=provinceList.get(position);
                    queryCity();
                }else if(currentLevel==LEVEL_CITY){
                    selectedCity=cityList.get(position);
                    queryCounties();
                }else if(currentLevel==LEVEL_COUNTY){
//                    查询天气
                    String weatherId=countyList.get(position).getWeatherId();

                    if(getActivity() instanceof MainActivity){
                        Intent intent=new Intent(getActivity(), WeatherActivity.class);
                        intent.putExtra("weather_id",weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    }else if(getActivity() instanceof WeatherActivity){
                        WeatherActivity weatherActivity= (WeatherActivity) getActivity();
                        weatherActivity.drawerLayout.closeDrawers();
                        weatherActivity.weatherSwipeRefreshLayout.setRefreshing(true);
                        weatherActivity.requestWeatherInfo(weatherId);
                    }
                }

            }
        });
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                根据当前等级，返回按钮返回上一级
                if(currentLevel==LEVEL_CITY) {
                    queryProvinces();
                }else if(currentLevel==LEVEL_COUNTY){
                    queryCity();
                }
            }
        });
    }

    /*
    * 查询所有省，优先从数据库查询，如果数据库没有就到服务器查询
    * */
    private void queryProvinces(){
        textTitle.setText("中国");
        buttonBack.setVisibility(View.GONE);
        provinceList= DataSupport.findAll(Province.class);
        if(provinceList.size()>0){
//            如果本地数据库有数据
            dataList.clear();
            for(Province province:provinceList){
                dataList.add(province.getProvinceName());
            }
//            数据改变，刷新列表
            adapter.notifyDataSetChanged();
            listViewCity.setSelection(0);//默认选中第一项

            currentLevel=LEVEL_PROVINCE;//当前等级是省

        }else{
            String address="http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }
    }
    /*
    * 查询所有市，优先从数据库查询，如果数据库没有就到服务器查询
    * */
    private void queryCity(){
        textTitle.setText(selectedProvince.getProvinceName());
        buttonBack.setVisibility(View.VISIBLE);
//        数据库查询当前省 的所有县区
        cityList=DataSupport.where("provinceId=?",String.valueOf(selectedProvince.getId())).find(City.class);
        if(cityList.size()>0){
            dataList.clear();
            for(City city:cityList){
                dataList.add(city.getCityName());
            }

            adapter.notifyDataSetChanged();
            currentLevel=LEVEL_CITY;
            listViewCity.setSelection(0);
        }else{
            int provinceId=selectedProvince.getProvinceCode();
            String address="http://guolin.tech/api/china/"+provinceId;
            queryFromServer(address,"city");
        }
    }
    /*
    * 查询所有区县，优先从数据库查询，如果数据库没有就到服务器查询
    * */
    private void queryCounties(){
        textTitle.setText(selectedCity.getCityName());
        countyList=DataSupport.where("cityId=?",String.valueOf(selectedCity.getId())).find(County.class);
//        Log.e("说说你有多大吧",""+countyList.size());
        if(countyList.size()>0){
            dataList.clear();
            for(County county:countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            currentLevel=LEVEL_COUNTY;
            listViewCity.setSelection(0);

        }else{
            int provinceCode=selectedProvince.getProvinceCode();
            int cityCode=selectedCity.getCityCode();
            String address="http://guolin.tech/api/china/"+provinceCode+"/"+cityCode;
//            Log.e("为什么非得说发给咱么了呢",""+address);
            queryFromServer(address,"county");
        }
    }

//      请求数据库数据,根据地址跟传入类型查询数据
    private void queryFromServer(String address, final String type) {
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
               getActivity().runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       dismissDialog();
                       Toast.makeText(getActivity(),"加载失败",Toast.LENGTH_SHORT).show();
                   }
               });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText=response.body().string();
                boolean result=false;
//                接受存入数据库是否成功的返回值Boolean

                switch (type){
                    case "province":
                        result= Utility.handleProvinceResponse(responseText);
                        break;
                    case "city":
                        result= Utility.handleCityResponse(responseText,selectedProvince.getId());
                        break;
                    case "county":
                        result= Utility.handleCountyResponse(responseText,selectedCity.getId());
                        break;
                }
//                如果成功将数据存入数据库
                if(result){
//                   dismissDialog();
//                    关闭加载进度条progressDialog，重新查询数据库加载数据，通过runOnUIthread返回主线程
                    getActivity()
                            .runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dismissDialog();
                                    switch (type){
                                        case "province":
                                            queryProvinces();
                                            break;
                                        case "city":
                                            queryCity();
                                            break;
                                        case "county":
                                            queryCounties();
                                            break;
                                    }
                                }
                            });
                    

                }
            }
        });
    }
//    关闭进度对话框

    private void dismissDialog() {
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }

    /*
    * 显示进度对话框
    * */
    private void showProgressDialog() {
        if(progressDialog==null){
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载。。");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }


}
