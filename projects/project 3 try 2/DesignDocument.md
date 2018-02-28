#Jack Kinne Project 3 - Google Vision

This app was made to demonstrate the annotations function in google vision!  Two activities have been made, to showcase this API call.  Picture is taken through the camera function, and API call is sent via thread so the UI is not affected.  Toasts are added on errors or lack of photo selection, Default picture is a sheep.

I wrote this application twice; which seems to be a theme.  Once I wrote the API call asyncrhonously, which failed (see P3v1).  The second time was with a thread.

#System Design
FIRST ACTIVITY PAGE:
the two buttons are:
1. "Take Picture" take a photo with the camera
2. "google vision" pass a photo into the API call

SECOND ACTIVITY PAGE: 
two textviews exist
1. "List of Things" title header for annotations from google.  
2. "1, 2, 3, 4, 5" which is a placeholder for the top returns.

#Usage
In the first activity, we see a default image and two buttons.
The first button, "take picture" takes a picture with the camera.  You can view the results before you continue.  

The second button, "google vision" takes the picture and uploads it to google's vision API, where annotations are generated on the fly for what it thinks the picture is, plus a percentage.  It transfers this information (and transfers the picture) to the second activity.

In the second activity we see the top ten items with percent for each of the annotation label returns from google vision.