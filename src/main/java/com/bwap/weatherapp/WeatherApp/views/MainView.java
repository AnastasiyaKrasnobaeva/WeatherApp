package com.bwap.weatherapp.WeatherApp.views;

import com.bwap.weatherapp.WeatherApp.controller.WeatherService;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ClassResource;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

@SpringUI(path = "")
public class MainView extends UI {
    @Autowired
    private WeatherService weatherService;

    private VerticalLayout mainLyout;
    private NativeSelect<String> unitSelect;
    private TextField cityTextField;
    private Button searchButton;
    private HorizontalLayout dashboard;
    private Label location;
    private Label currentTemp;
    private HorizontalLayout mainDescriptionLayout;
    private Label weatherDescription;
    private Label MaxWeather;
    private Label MinWeather;
    private Label Humidity;
    private Label Pressure;
    private Label Wind;
    private Label FeelsLike;
    private Image iconImg = new Image();

    public MainView(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        mainlayout();
        setHeader();
        setLogo();
        setForm();
        dashboardTitle();
        dashboardDetails();
        searchButton.addClickListener(clickEvent -> {
            if (!cityTextField.getValue().equals("")) {
                updateUI();// try/catch
            } else
                Notification.show("Please Enter The City Name");
        });

    }

    private void mainlayout() {
        mainLyout = new VerticalLayout();
        mainLyout.setWidth("100%");
        mainLyout.setSpacing(true);
        mainLyout.setMargin(true);
        mainLyout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        setContent(mainLyout);

    }

    private void setHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        Label title = new Label("Weather");

        header.addComponent(title);

        mainLyout.addComponent(header);
    }

    private void setLogo() {
        HorizontalLayout logo = new HorizontalLayout();
        logo.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        Image img = new Image(null, new ClassResource("/logo.png"));
        img.setWidth("240px");
        img.setHeight("240px");
        logo.setWidth("240px");
        logo.setHeight("240px");

        logo.addComponent(img);
        mainLyout.addComponent(logo);
    }

    private void setForm() {
        HorizontalLayout formLayout = new HorizontalLayout();
        formLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        formLayout.setSpacing(true);
        formLayout.setMargin(true);

        unitSelect = new NativeSelect<>();
        ArrayList<String> items = new ArrayList<>();
        items.add("C");
        items.add("F");

        unitSelect.setItems(items);
        unitSelect.setValue(items.get(0));
        formLayout.addComponent(unitSelect);

        cityTextField = new TextField();
        cityTextField.setWidth("80%");
        formLayout.addComponent(cityTextField);


        searchButton = new Button();
        searchButton.setIcon(VaadinIcons.SEARCH);
        formLayout.addComponent(searchButton);


        mainLyout.addComponent(formLayout);


    }

    private void dashboardTitle() {

        dashboard = new HorizontalLayout();
        dashboard.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);


        location = new Label("Currently in Minsk");
        location.addStyleName(ValoTheme.LABEL_H2);
        location.addStyleName(ValoTheme.LABEL_LIGHT);


        currentTemp = new Label("10C");
        currentTemp.setStyleName(ValoTheme.LABEL_BOLD);
        currentTemp.setStyleName(ValoTheme.LABEL_H1);

        dashboard.addComponents(location, iconImg, currentTemp);
        mainLyout.addComponent(dashboard);


    }

    private void dashboardDetails() {
        mainDescriptionLayout = new HorizontalLayout();
        mainDescriptionLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);


        VerticalLayout descriptionLayout = new VerticalLayout();
        descriptionLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        weatherDescription = new Label("DescriptionL: Clear Skies");
        weatherDescription.setStyleName(ValoTheme.LABEL_SUCCESS);
        descriptionLayout.addComponents(weatherDescription);


        MinWeather = new Label("Min:53");
        descriptionLayout.addComponents(MinWeather);

        MaxWeather = new Label("Min:53");
        descriptionLayout.addComponents(MaxWeather);

        VerticalLayout pressureLayout = new VerticalLayout();
        pressureLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        Pressure = new Label("Pressure: 231Pa");
        pressureLayout.addComponents(Pressure);

        Humidity = new Label("Humidity: 231Pa");
        pressureLayout.addComponents(Humidity);

        Wind = new Label("Wind: 231Pa");
        pressureLayout.addComponents(Wind);

        FeelsLike = new Label("FeelsLike: 231Pa");
        pressureLayout.addComponents(FeelsLike);


        mainDescriptionLayout.addComponents(descriptionLayout, pressureLayout);


    }

    private void updateUI() {
        String city = cityTextField.getValue();
        String defaultUnit;
        weatherService.setCityName(city);

        if (unitSelect.getValue().equals("F")) {
            weatherService.setUnit("imperials");
            unitSelect.setValue("F");
            defaultUnit = "\u00b0" + "F";
        } else {
            weatherService.setUnit("metric");
            defaultUnit = "\u00b0" + "C";
            unitSelect.setValue("C");

        }


        location.setValue("Currently in " + city);
        JSONObject mainObject = weatherService.returnMain();
        int temp = mainObject.getInt("temp"); // не ключ
        currentTemp.setValue(temp + defaultUnit);

        String iconCode = null;
        String weatherDescriptionNew = null;
        JSONArray jsonArray = weatherService.returnWeatherArray();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject weatherObj = jsonArray.getJSONObject(i);
            iconCode = weatherObj.getString("icon");
            weatherDescriptionNew = weatherObj.getString("description");

        }
        iconImg.setSource(new ExternalResource("http://openweathermap.org/img/wn/" + iconCode + "@2x.png"));

        weatherDescription.setValue("Description: " + weatherDescriptionNew);
        MinWeather.setValue("Min Temp: " + weatherService.returnMain().getInt("temp_min") + unitSelect.getValue());
        MaxWeather.setValue("Max Temp: " + weatherService.returnMain().getInt("temp_max") + unitSelect.getValue());
        Pressure.setValue("Pressure: " + weatherService.returnMain().getInt("pressure"));
        Humidity.setValue("Humidity: " + weatherService.returnMain().getInt("humidity"));
        FeelsLike.setValue("Feels like: " + weatherService.returnMain().getInt("feels_like"));

        mainLyout.addComponents(dashboard, mainDescriptionLayout);


    }
}

