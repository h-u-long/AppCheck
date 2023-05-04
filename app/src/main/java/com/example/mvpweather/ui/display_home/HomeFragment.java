package com.example.mvpweather.ui.display_home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.mvpweather.model.weather_current.WeatherCurrent;
import com.example.mvpweather.databinding.FragmentHomeBinding;
import com.example.mvpweather.utils.KeyConstants;
import com.example.mvpweather.utils.KeyTemF;
import com.example.mvpweather.utils.KeyboardUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeFragment extends Fragment implements HomeInterface{
    private FragmentHomeBinding binding;
    private View view;
    private DecimalFormat df = new DecimalFormat("#");
    private HomePresenter homePresenter;

    @Override
    //khởi tạo lớp HomePresenter.
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homePresenter = new HomePresenter(this);

    }

    @Override
    //lớp inflate layout của fragment bằng cách sử dụng
    // lớp FragmentHomeBinding.
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater,container,false);
        view = binding.getRoot();
        return view;
    }

    @Override
    //lớp thiết lập click listener cho các button và gọi phương thức
    // setCityDefault với một thành phố mặc định ("Huế").
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        selectDown();
        setCityDefault("Huế");
        clickListener();
    }
//thiết lập click listener cho nút tìm kiếm và gọi các phương thức
// sentDataToHomePresenter và updateViewSearch.
    private void clickListener() {
            binding.buttonSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sentDataToHomePresenter();
                    updateViewSearch();
                }
            });
    }
    //lấy một tham số tên thành phố và chuyển nó, cùng với một khóa API,
    // đến phương thức getDataByFragmentHome của HomePresenter để lấy dữ liệu thời tiết từ một API.
    private void setCityDefault(String city) {
        homePresenter.getDataByFragmentHome(city, KeyConstants.APIKEY);
    }
//lấy văn bản từ EditText tìm kiếm, trim nó và chuyển nó, cùng với một khóa API,
// đến phương thức getDataByFragmentHome của HomePresenter để lấy dữ liệu thời tiết từ một API.
    private void sentDataToHomePresenter() {
        String city = binding.edittextSearch.getText().toString().trim();
        homePresenter.getDataByFragmentHome(city, KeyConstants.APIKEY);
    }
//cập nhật sự hiển thị của các layout group,
// ẩn bàn phím và làm trống EditText.
    private void updateViewSearch(){
        binding.groudSearch.setVisibility(View.GONE);
        binding.groupCity.setVisibility(View.VISIBLE);
        KeyboardUtils.hideKeyboardFrom(getContext(), view);
        binding.edittextSearch.setText("");
    }
//trả về văn bản của TextView của thành phố.
    public String getCityName(){
        if(binding != null){
            return binding.textviewCity.getText().toString();
        }
        return "";
    }
//thiết lập click listener cho nút thả xuống
// và cập nhật sự hiển thị của các layout group.
    private void selectDown() {
            binding.imageDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    binding.groupCity.setVisibility(View.GONE);
                    binding.groudSearch.setVisibility(View.VISIBLE);
                }
            });
    }

    //Nguyên Lý S.O.L.I.D
    @Override
   // lớp triển khai phương thức HomeInterface để cập nhật giao diện người dùng
    // với dữ liệu thời tiết được lấy từ API.
    // Nó kiểm tra xem dữ liệu có khác null hay không và cập nhật TextViews, ImageView và các view khác với dữ liệu được lấy
    public void onSuccess(WeatherCurrent data) {
        if (data.getName() != null && binding != null){
            binding.textviewCity.setText(data.getName());
            Date date = new Date(Long.valueOf(data.getDt()) * 1000L);
            SimpleDateFormat sp = new SimpleDateFormat("EE yyyy-MM-dd HH-mm-ss");
            binding.textviewCurrentTime.setText(sp.format(date));
            binding.textviewTemperature.setText(String.valueOf(df.format((data.getMain().getTemp()) - KeyTemF.TEMF)));
            binding.textviewTempFeels.setText(String.valueOf(df.format((data.getMain().getFeels_like()) - KeyTemF.TEMF)));
            binding.textviewDetailHumidity.setText(df.format(data.getMain().getHumidity()));
            binding.textviewDetailWindspeed.setText(String.valueOf(data.getWind().getSpeed()));
            binding.textviewDescription.setText(data.getWeather().get(0).getDescription().toString());
            String icon = data.getWeather().get(0).getIcon();
            Glide.with(getContext())
                    .load("http://openweathermap.org/img/wn/" + icon + ".png").into(binding.imageviewIconWeather);
        } else return;
    }
//triển khai phương thức HomeInterface để hiển thị một tin nhắn toast với một thông báo lỗi nếu cuộc gọi API thất bại.
// Nó kiểm tra xem thông báo lỗi có khác null hay không và hiển thị tin nhắn toast nếu không thì trả về.
    @Override
    public void onFailed(String error) {
        if (error != null) {
            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        } else return;
    }
}

