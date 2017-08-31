package com.heweater;

import android.app.ProgressDialog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.heweater.db.City;
import com.heweater.db.County;
import com.heweater.db.Province;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import static com.heweater.util.Utility.sendRequestWithHttpURLConnection;

public class MainActivity extends AppCompatActivity {
    private Button createDataBase;
    private Button readDataBase;
    public static final int LEVEL_PROVINCE = 0;

    public static final int LEVEL_CITY = 1;

    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;

    private TextView titleText;
    private Button backButton;

    private ListView listView;

    private ArrayAdapter adapter;

    private List<String> dataList = new ArrayList<>();

    /**
     * 省列表
     */
    private List<Province> provinceList;

    /**
     * 市列表
     */
    private List<City> cityList;

    /**
     * 县列表
     */
    private List<County> countyList;

    /**
     * 选中的省份
     */
    private Province selectedProvince;

    /**
     * 选中的城市
     */
    private City selectedCity;

    /**
     * 当前选中的级别
     */
    private int currentLevel;
    private boolean onlyOnecity;
    private List<String> provinces;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        backButton = (Button) findViewById(R.id.back_button);
        titleText = (TextView) findViewById(R.id.title_text);
        listView = (ListView) findViewById(R.id.country_lvcountry);
        createDataBase = (Button) findViewById(R.id.createdatabase);
        readDataBase = (Button) findViewById(R.id.readdata);
        listView = (ListView) findViewById(R.id.country_lvcountry);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        createDataBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataSupport.deleteAll("province");
                DataSupport.deleteAll("city");
                DataSupport.deleteAll("county");
                String url = "https://cdn.heweather.com/china-city-list.txt";
                sendRequestWithHttpURLConnection(url);


            }
        });
        readDataBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryProvinces();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position );
                    queryCounties();
                }else if(currentLevel == LEVEL_COUNTY){
                    String weatherId = countyList.get(position ).getWeatherId();
                    Toast.makeText(MainActivity.this,weatherId,Toast.LENGTH_LONG).show();

                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((currentLevel == LEVEL_CITY) || onlyOnecity) {
                    queryProvinces();
                } else if (currentLevel == LEVEL_COUNTY) {
                    queryCities();
                }

            }
        });


    }
    /**
     * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryProvinces() {
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        }
    }

    /**
     * 查询选中省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryCities() {
        cityList = DataSupport.where("provinceid = ?", String.valueOf(selectedProvince.getId())).find(City.class);
        if(cityList.size() == 1){
            titleText.setText(selectedProvince.getProvinceName());
            backButton.setVisibility(View.VISIBLE);
            onlyOnecity = true;
            selectedCity = cityList.get(0);
            queryCounties();
        }
        else if (cityList.size() > 1) {
            titleText.setText(selectedProvince.getProvinceName());
            backButton.setVisibility(View.VISIBLE);
            onlyOnecity = false;
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;



        }
    }

    /**
     * 查询选中市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryCounties() {
        countyList = DataSupport.where("cityid = ?", String.valueOf(selectedCity.getId())).find(County.class);
        if(countyList.size()  == 1){
            String weatherId = countyList.get(0).getWeatherId();
            Toast.makeText(MainActivity.this,weatherId,Toast.LENGTH_LONG).show();

        }else if (countyList.size() > 1) {
            titleText.setText(selectedCity.getCityName());
            backButton.setVisibility(View.VISIBLE);
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        }
    }



}
