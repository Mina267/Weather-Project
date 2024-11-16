# Weather-Project

## Project Overview

**Weather Forecast Android Application**  
A real-time weather forecasting app that provides temperature, humidity, and wind data, plus custom alerts for significant weather changes. Users can manage favorite locations, personalize settings, and choose between GPS-based or manual location input for more control.


<p align="center">
	<img src="https://github.com/user-attachments/assets/cf680241-fceb-4fb0-af3c-cd5241cd6d77" width=60% height=60% />
</p>

---

## Features




### 1. Live Weather (Home Screen)
   - **Current Weather Display**: Shows temperature, humidity, wind speed, pressure, cloud coverage, city name, and an icon representing current weather conditions.
   - **Weather Description**: Provides detailed descriptions like “clear sky” or “light rain” for easy interpretation.
   - **24-Hour and 7-Day Forecast**: Displays weather forecasts for the next 24 hours and the upcoming 7 days.
   - **Sunrise and Sunset Animations**: Includes animated transitions to visually indicate sunrise and sunset times.

   <p align="center">
      <img src="https://github.com/user-attachments/assets/4a86cc10-fe8d-4f50-8e50-3e833a60cc76" style="width: 19%; display: inline-block; vertical-align: middle; margin-right: 2%;" />
      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
      <img src="https://github.com/user-attachments/assets/bf1d8b3e-8096-4a4b-b7ea-1b98a9979626" style="width: 17%; display: inline-block; vertical-align: middle;" />
   </p>

---

Let me know if this aligns with your vision for the README or if there’s anything else you’d like to add!

### 2. Alerts (Alerts Screen)
   - **Set Weather Alerts**: Configure alerts for specific weather conditions (e.g., rain, fog, extreme temperatures).
   - **Alert Settings**: Customize the alert duration and choose between notification alerts or sound alarms.
   - **Alert Management**: Options to silence or turn off alerts directly from the app.

   <p align="center">
      <img src="https://github.com/user-attachments/assets/93cf938e-3a39-4652-b6e4-76c93b8b23f3" style="width: 19%; display: inline-block; vertical-align: middle; margin-right: 2%;" />
      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
      <img src="https://github.com/user-attachments/assets/c2bf4fc5-0268-4cf4-830e-b10782658b2a" style="width: 17%; display: inline-block; vertical-align: middle;" />
   </p>

---

### 3. Favorites Management (Favorites Screen)
   - **Add Favorite Locations**: Save locations by GPS, map marker, or search with autocomplete, with access to detailed weather data for each.
   - **Quick Access**: Lists favorite locations, allowing a single tap to open forecast details.
   - **Delete Option**: Remove saved locations as needed.

      <p align="center">
      <img src="https://github.com/user-attachments/assets/9447b0c1-d953-4989-98bd-2307eb936f3e" style="width: 19%; display: inline-block; vertical-align: middle; margin-right: 2%;" />
      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
      <img src="https://github.com/user-attachments/assets/01ed16de-f010-465d-a134-e091f08c8b30" style="width: 17%; display: inline-block; vertical-align: middle;" />
      </p>

---

### 4. Settings Customization (Settings Screen)
   - **Units Selection**: Choose temperature units (Celsius, Fahrenheit, Kelvin) and wind speed units (m/s, mph).
   - **Language Options**: Select between Arabic and English for full localization.
   - **Location Source**: Use GPS-based data or manually select locations on the map.

      <p align="center">
      <img src="https://github.com/user-attachments/assets/5186d6d5-538b-43fc-9342-d0266153a63e" style="width: 19%; display: inline-block; vertical-align: middle; margin-right: 2%;" />
      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
      <img src="https://github.com/user-attachments/assets/b2033f00-44ec-4fa3-b4f9-d92f21bc27ef" style="width: 17%; display: inline-block; vertical-align: middle;" />
   </p>


---

## Technologies & Implementation

### 1. **Architecture: MVVM (Model-View-ViewModel)**
   - **Separation of Concerns**: Adopts the MVVM pattern to separate data handling, business logic, and UI updates.
   - **ViewModel**: Manages UI-related data in a lifecycle-conscious way, ensuring data persists across configuration changes.
   - **LiveData & Flow**: Uses LiveData and Flow for observing data changes, with StateFlow for consistent state handling across configuration changes.
      <p align="center">
      <img src="https://github.com/user-attachments/assets/099c29c6-8f79-44b9-8f86-bbb92ad1b64e" style="width: 19%; display: inline-block; vertical-align: middle; margin-right: 2%;" />
      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
      <img src="https://github.com/user-attachments/assets/20b35cb3-2d95-4415-a184-5653f6fce420" style="width: 17%; display: inline-block; vertical-align: middle;" />
   </p>

### 2. **Asynchronous Programming: Kotlin Coroutines, Flow, and StateFlow**
   - **Concurrency Management**: Handles long-running tasks using Kotlin Coroutines to ensure a responsive UI.
   - **Flow & StateFlow**: Leverages Flow and StateFlow for handling continuous streams of data and state, respectively.
   - **Sealed Classes**: Implements sealed classes for structured API responses, managing success, loading, and error states.
   <p align="center">
      <img src="https://github.com/user-attachments/assets/dd53e87d-53c1-4ee6-aa37-95ddeb871ce4" style="width: 19%; display: inline-block; vertical-align: middle; margin-right: 2%;" />
      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
      <img src="https://github.com/user-attachments/assets/31f5019f-39b1-4a6e-b9a1-df71cf49be81" style="width: 17%; display: inline-block; vertical-align: middle;" />
   </p>

### 3. **Networking: Retrofit & OpenWeatherMap API**
   - **Data Retrieval**: Uses Retrofit to fetch live weather data from the One Call API.
   - **JSON Parsing**: Parses JSON data into Kotlin data classes using Retrofit's Gson converter.
   - **Error Handling**: Manages exceptions and errors with structured handling, enhancing app stability.
   <p align="center">
      <img src="https://github.com/user-attachments/assets/e32c04c6-9130-4ad9-a358-689fbc6bc86a" style="width: 19%; display: inline-block; vertical-align: middle; margin-right: 2%;" />
      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
      <img src="https://github.com/user-attachments/assets/d2257943-e4e8-4eb0-a51c-417f44f2668d" style="width: 17%; display: inline-block; vertical-align: middle;" />
   </p>

### 4. **Location Management: Location Manager**
   - **Location Services**: Retrieves device location via the Android Location Manager for accurate weather data based on the user’s position.
   - **GPS & Map Selection**: Allows users to choose between GPS-based location or manual location selection on a map.
   <p align="center">
      <img src="https://github.com/user-attachments/assets/f5cdaed3-423b-4a95-bee9-cf100e894d40" style="width: 19%; display: inline-block; vertical-align: middle; margin-right: 2%;" />
      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
      <img src="https://github.com/user-attachments/assets/a7f41863-20e9-46c4-9144-b733ddb0d0b1" style="width: 17%; display: inline-block; vertical-align: middle;" />
   </p>

### 5. **Local Storage: Room Database**
   - **Persistence Layer**: Stores weather data, favorite locations, and alert configurations.
   - **TypeConverters**: Supports complex data types in Room, handling lists and other structures.
   - **Caching**: Stores weather data in memory and caches accordingly.
   - **Restore**: Stores weather data in memory and caches accordingly.
   - **Data Access Objects (DAOs)**: Simplifies data retrieval and modification.
   <p align="center">
      <img src="https://github.com/user-attachments/assets/4d6f87e8-ea8f-4065-9afd-2d56d4224519" style="width: 19%; display: inline-block; vertical-align: middle; margin-right: 2%;" />
      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
      <img src="https://github.com/user-attachments/assets/f0f90d8d-38b5-441c-97b9-7816b1f03ed5" style="width: 17%; display: inline-block; vertical-align: middle;" />
   </p>

### 6. **User Preferences: Shared Preferences**
   - **Settings Management**: Stores user settings such as units, language, and alerts.
   - **Localization**: Supports Arabic and English, dynamically changing the language.
   <p align="center">
      <img src="https://github.com/user-attachments/assets/7c37bdff-db17-4beb-a27d-86b98e023ab0" style="width: 19%; display: inline-block; vertical-align: middle; margin-right: 2%;" />
      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
      <img src="https://github.com/user-attachments/assets/3839492a-72ef-4a9a-8e3a-d3a55b1951ce" style="width: 17%; display: inline-block; vertical-align: middle;" />
   </p>

### 7. **Background Work: Work Manager & Alarm Manager**
   - **Regular Weather Updates**: Keeps weather information up-to-date in the background.
   - **Weather Alerts**: Customizes alerts for user-defined weather conditions through Alarm Manager.
   <p align="center">
	   <img src="https://github.com/user-attachments/assets/213496d0-8686-4c16-a699-dfaa95b1cd14" width=60% height=60% />
   </p>

### 8. **Testing: Unit and Instrumentation Tests**
   - **Unit Testing**: Verifies individual components, especially ViewModel and data-handling layers, ensuring correctness.
   - **Instrumentation Testing**: Tests UI and interactions in an actual or emulated Android environment.
     <p align="center">
	   <img src="https://github.com/user-attachments/assets/c352abcc-db89-4bf1-9e01-e40fde3ae78c" width=60% height=60% />
   </p>

### 9. **User Interface Components**
   - **Open street maps API**: Integrates map-based location selection.
   - **Animations**: Adds effects for sunrise and sunset transitions to improve the visual experience.

   <p align="center">
	<img src="https://github.com/user-attachments/assets/93e793ec-98c2-4917-8c74-d3dd61dba790" width=60% height=60% />
</p>


---

