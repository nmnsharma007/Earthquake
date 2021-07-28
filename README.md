# Earthquake
## Android App for fetching earthquake data.

The App is made using Java language. It uses an earthquake api to fetch recent earthquake information
and display it to the user using a recycler view.
The information displayed includes: 
- Earthquake Magnitude
- Earthquake Location
- Date and Time of occurrence 

The network call is made on a background thread 
and the list of earthquakes is fetched. The app also has a settings activity through which the user
can change their preferences on earthquakes to be displayed. The user can choose the minimum magnitude
and maximum magnitude and also sort the earthquake information based on ascending magnitude, descending
magnitude, ascending time and descending time. The app uses Shared Preferences to store this information
and saves it even when the app is closed. The App also uses ViewModel so that network request is not made
again when the device configuration changes like rotation of the screen. The App also uses LiveData to 
track the changes in the internet connectivity or user preferences so that a new network request is 
automatically made.
