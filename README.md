# Simple Orbit Sim in 2D
Just a basic simulator in 2D using Newton's model of gravity. This is no Kerbal Space Program, but can get the point across on how orbits will work if its the dominant factor. 
## How to Use
The sliders are for adjusting horizontal and vertical components for the initial velocity (except the brown slider). Click anywhere on screen to change the starting point. The brown slider changes the mass of the center.
When you have the starting conditions you want, click the "Start Over" button.
## Problems
Sometimes orbits will shift over multiple iterations If the free mass hits the center of the orbited mass many unrealistic situations can occur.
Many problems stem from how it's simply adding the velocity to the position value at the end of each frame. The simulation performs quite well despite this.