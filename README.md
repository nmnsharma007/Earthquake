# Earthquake
Android App made for fetching earthquake data
The App is made using Java language. It uses an earthquake api to fetch the earthquake information
and display it to the user using a recycler view. The network call is made on a background thread 
and the list of earthquakes is fetched. The app also has a settings activity through which the user
can change their preferences on earthquakes to be displayed. The user can choose the minimum magnitude
and maximum magnitude and also sort the earthquake information based on ascending magnitude, descending
magnitude, ascending time and descending time. This information once set will be saved even after user
closes the app and opens it again. The app makes the api call only when the user preferenes have changed.
