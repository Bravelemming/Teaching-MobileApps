# Jack Kinne Project 3 - Google Vision plus Cleanup

This app was made to demonstrate the annotations function in google vision!  Two activities have been made, to showcase this API call.  Picture is taken through the camera function, and API call is sent via thread so the UI is not affected.  Toasts are added on errors or lack of photo selection, Default picture is a sheep.

I wrote this application twice; which seems to be a theme.  Once I wrote the API call asynchronously, which failed (see P3v1).  The second time was with a thread.

---

# UPDATE FOR PROJECT 4:

Many bugs were targetted and fixed.

- Fixed a bug that would crash the camera on some later (API 25 and above) phone OS's.  

- Fixed a bug on the async call that would crash the app, due to needing an effect on the UI thread.  

- Improved the third activity page and made the results display set in multiple textviews, instead of just a simple toast.  

- Added the image to the results by passing it through the intent.

- Added a button that functionally resets the app, clearing the data and enabling more async API calls.  

---

# System Design

## FIRST ACTIVITY PAGE:

the two buttons are:
1. "Take Picture" take a photo with the camera
2. "google vision" pass a photo into the API call

## SECOND ACTIVITY PAGE: 

A picture confirming the one taken exists. 
the two buttons are:
1. "Take Picture" take a photo with the camera
2. "google vision" pass a photo into the API call


## THIRD ACTIVITY PAGE:

1. "Google thought this was:" title header for annotations from google.  

2. three text views exist: 
 - annotation label[0] + " : " + annotation percentage.
 - annotation label[1] + " : " + annotation percentage.
 - annotation label[2] + " : " + annotation percentage.

3. A picture confirming the image tested.

4. A button labelled "Let's do it again!".  
 - returns user not just to the first activity, but RESETS app.

---  

# Usage

In the first activity, we see a default image and two buttons.
The first button, "take picture" takes a picture with the camera.  You can view the results before you continue.  

Clicking the "google vision" button without a picture will toast you a warning that you haven't selected an image to annotate, and not trigger the effect.

The second button, "google vision" takes the picture and uploads it to google's vision API, where annotations are generated on the fly for what it thinks the picture is, plus a percentage.  It transfers this information (and transfers the picture) to the third activity.

In the third activity we see the top three items with percent for each of the annotation label returns, as well as the image and a button marked, "let's do it again!".

clicking "let's do it again!" functionally resets the app back to its beginning state through an intent, enabling mutliple uses of the app.