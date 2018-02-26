# Post Mortem
//Jack Daniel Kinne // P2

There were some challenges in this latest assignment -- particularly self imposed ones.  I started this assignment with Xamarin Visual Studio and had a working --and ugly--version of the app.  For many reasons, I elected to throw away my progress and jump onto Android Studio.  It was worth it. 
One particular struggle I spent some time grappling with was how to manipulate multiple effects onto a bitmap.  More specifically, I couldn't even modify one effect, at first!  True story: when bitmaps are transfered across activities (which was its own 30 minute deep dive on how to do that) they come as an immutable object.  And That was fun.  Eventually I found the error in my logcat Emulator output: that the bitmap was in fact, immutable.  Some more googleFu later, and I had my answer: "Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);"  That last parameter controlled the mutability of the bitmap.  Once I could modify the bitmap, I took some time to chart exactly how I could apply mutliple effects, and more importantly undo them when they were deselected on the switches.  That particular effect I am particularly proud of.  
Each of the photo effects I made took a copy of the bitmap, and were called in a cascade that was reversable, only checked once, and didn't waste space in replicating any additional bitmaps -- only the single copy that was passed back and forth between them. And the app is quick as sin in its third iteration --previously I had made a bitmap for each effect, and discarded them on reverting changes.  
Now, even the revert button doesn't make a new copy -- it just repoints back to the original.  

An issue I was unable to resolve was that darn layout.  To be clear:  I achieved a solution that was undeployable due to its timeframe.  My problem was that I discovered my problem too late.  My activity layout was originally made in a "basic" activity I selected to give me a template I could start developing with.  And the basic activity starts with this type in the xml: "android.support.constraint.ConstraintLayout".  One large issue with a constraint layout is that its constrained with needing specific DP's targetted for all objects you can see.  There are two solutions people use with this. They make a UI for one of each type of screen sizes; small, medium, large, and extralarge. Or, alternatively, you can use a different layout type: "RelativeLayout".   When I switched to this new layout type, I realised that I had to completely redo all my objects-- or Alternatively make three new constraint layouts for all types.  And I didn't want to devote that much time to fix a mistake I had made for very little gain (two of the switch buttons are offcenter, and Landscape mode doesn't look good.)  Lesson learned for the future, though: constraint is terrible, and always start with a relative layout for ease, unless i'm planning to put a professional amount of work in using constraints.
The best advice I can give to future students is this: always, always consider your layout type before you spend hours working in one.  And if you do nothing else, work modularly.  I spent time getting one very very small thing to work, and then added one thing to it, and reguarly rebuilt the code.  The compiler told me, frequently, what was wrong, and I knew exactly what I had to do, or to research, to find a solution.  
By the time I had the small pieces working, they added to something bigger.
And the rest of the project kind of snowballed from there.  Small, measurable goals are more worthwhile than an unwieldy, and more importantly less-understood mess.  

The most fun I had with the project was writing the logical control code to cycle through activity two.  I had a great time working with the slider controls, trying to make them work in a cascade, with reversibility built in.  It was a logic and scope puzzle; and I played with some dry erase and a whiteboard --which made for a good break to staring at the screen.