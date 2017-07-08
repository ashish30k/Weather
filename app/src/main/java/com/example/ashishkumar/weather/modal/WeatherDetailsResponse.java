
package com.example.ashishkumar.weather.modal;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WeatherDetailsResponse implements Parcelable {

    @SerializedName("coord")
    @Expose
    private Coord coord;
    @SerializedName("weather")
    @Expose
    private List<Weather> weather = null;
    @SerializedName("base")
    @Expose
    private String base;
    @SerializedName("main")
    @Expose
    private Main main;
    @SerializedName("visibility")
    @Expose
    private int visibility;
    @SerializedName("wind")
    @Expose
    private Wind wind;
    @SerializedName("clouds")
    @Expose
    private Clouds clouds;
    @SerializedName("dt")
    @Expose
    private double dt;
    @SerializedName("sys")
    @Expose
    private Sys sys;
    @SerializedName("id")
    @Expose
    private double id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("cod")
    @Expose
    private int cod;

    protected WeatherDetailsResponse(Parcel in) {
        base = in.readString();
        visibility = in.readInt();
        dt = in.readDouble();
        id = in.readDouble();
        name = in.readString();
        cod = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(base);
        dest.writeInt(visibility);
        dest.writeDouble(dt);
        dest.writeDouble(id);
        dest.writeString(name);
        dest.writeInt(cod);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<WeatherDetailsResponse> CREATOR = new Creator<WeatherDetailsResponse>() {
        @Override
        public WeatherDetailsResponse createFromParcel(Parcel in) {
            return new WeatherDetailsResponse(in);
        }

        @Override
        public WeatherDetailsResponse[] newArray(int size) {
            return new WeatherDetailsResponse[size];
        }
    };

    public Coord getCoord() {
        return coord;
    }

    public void setCoord(Coord coord) {
        this.coord = coord;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public void setWeather(List<Weather> weather) {
        this.weather = weather;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public Clouds getClouds() {
        return clouds;
    }

    public void setClouds(Clouds clouds) {
        this.clouds = clouds;
    }

    public double getDt() {
        return dt;
    }

    public void setDt(double dt) {
        this.dt = dt;
    }

    public Sys getSys() {
        return sys;
    }

    public void setSys(Sys sys) {
        this.sys = sys;
    }

    public double getId() {
        return id;
    }

    public void setId(double id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCod() {
        return cod;
    }

    public void setCod(int cod) {
        this.cod = cod;
    }

    public static WeatherDetailsResponse parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        WeatherDetailsResponse boxOfficeMovieResponse = gson.fromJson(response, WeatherDetailsResponse.class);
        return boxOfficeMovieResponse;
    }

}
