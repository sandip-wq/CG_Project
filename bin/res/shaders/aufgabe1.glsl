#version 330

// We'll output RGBA color for each pixel.
out vec4 fragColor;

// --------------------------------------------------
// 1) Helper function: "istImKreis"
//    Returns true if 'punkt' is within 'radius' of 'zentrum'.
// --------------------------------------------------
bool istImKreis(vec2 punkt, vec2 zentrum, float radius) {
    return distance(punkt, zentrum) <= radius;
}

// --------------------------------------------------
// 5) Helper function for drawing lines:
//    Returns the distance from 'p' to the *infinite* line
//    passing through 'p1' and 'p2'. If you want a
//    line *segment*, you'd need a bit more logic.
// --------------------------------------------------
float distanceToLine(vec2 p, vec2 p1, vec2 p2) {
    // Vector form
    vec2  v  = p2 - p1;       // direction of line
    vec2  w  = p  - p1;       // vector from p1 to p
    float c1 = dot(w, v) / dot(v, v);

    // For an infinite line, c1 can be anything. If you only want
    // a line *segment*, you'd clamp c1 to [0,1].
    // Here we keep it infinite for simplicity.

    // Closest point on the infinite line = p1 + c1*v
    vec2  proj = p1 + c1 * v;
    // Distance from p to that point:
    return distance(p, proj);
}

// --------------------------------------------------
// Main fragment shader
// --------------------------------------------------
void main()
{
    //-------------------------------------
    // A) Background color
    //-------------------------------------
    fragColor = vec4(0.9, 0.9, 0.9, 1.0);  // Light gray background

    //-------------------------------------
    // B) Current pixel coordinates
    //-------------------------------------
    vec2 pixelPos = gl_FragCoord.xy;  // e.g. [0..700, 0..700] if window is 700×700

    //-------------------------------------
    // 1) Axis-aligned rectangle
    //
    //    Let's define an AABB from (50,50) to (200,150).
    //    If pixelPos is inside, color it red.
    //-------------------------------------
    if (pixelPos.x >= 50.0 && pixelPos.x <= 200.0 &&
        pixelPos.y >= 50.0 && pixelPos.y <= 150.0)
    {
        fragColor = vec4(1.0, 0.0, 0.0, 1.0); // red
    }

    //-------------------------------------
    // 2) Filled circle using istImKreis
    //
    //    Circle center: (600, 600)
    //    Radius: 70
    //    Color: green
    //-------------------------------------
    if (istImKreis(pixelPos, vec2(600.0, 600.0), 70.0)) {
        fragColor = vec4(0.0, 1.0, 0.0, 1.0); // green
    }

    //-------------------------------------
    // 3) Multiple shapes simultaneously
    //    - Let's do another circle & another rectangle
    //      in different positions/colors.
    //-------------------------------------

    // Another circle at (200, 500), radius 40, color blue
    if (istImKreis(pixelPos, vec2(200.0, 500.0), 40.0)) {
        fragColor = vec4(0.0, 0.0, 1.0, 1.0); // blue
    }

    // Another rectangle from (500,100) to (650,180), color orange
    if (pixelPos.x >= 500.0 && pixelPos.x <= 650.0 &&
        pixelPos.y >= 100.0 && pixelPos.y <= 180.0)
    {
        fragColor = vec4(1.0, 0.5, 0.0, 1.0); // orange
    }

    //-------------------------------------
    // 4) Rotated rectangle by angle w
    //    We'll do center-based "unrotation".
    //
    //    Let's define a rectangle with center (350,350),
    //    half-size (100,50), rotated by 30° about its center.
    //-------------------------------------
    // Rotation angle in degrees → radians
    float angleDeg = 30.0;
    float angleRad = radians(angleDeg);

    // We'll "unrotate" the pixel coordinate about the rect center
    vec2 rectCenter   = vec2(350.0, 350.0);
    vec2 halfSize     = vec2(100.0, 50.0);   // half width, half height

    // Build a rotation matrix for +angle
    mat2 rot = mat2(
         cos(angleRad), -sin(angleRad),
         sin(angleRad),  cos(angleRad)
    );

    // Shift pixel so rectCenter is at the origin
    vec2 localPos = pixelPos - rectCenter;

    // Now un-rotate by -angle, i.e. multiply by rot^(-1),
    // but for a pure rotation matrix, rot^(-1) = rot^T
    // or just define a matrix that rotates by +angle, 
    // but conceptually let's call it unrotate:
    vec2 unrotatedPos = rot * localPos;

    // Check if inside the axis-aligned bounding box
    if (abs(unrotatedPos.x) <= halfSize.x &&
        abs(unrotatedPos.y) <= halfSize.y)
    {
        // Pink rectangle
        fragColor = vec4(1.0, 0.0, 1.0, 1.0); // magenta/pink
    }

    //-------------------------------------
    // 5) Draw a line by checking the distance
    //    to an infinite line or segment.
    //
    //    Let's say line from p1=(100,300) to p2=(600,450).
    //    If distance < 3.0, we color it black.
    //-------------------------------------
    vec2 p1 = vec2(100.0, 300.0);
    vec2 p2 = vec2(600.0, 450.0);

    float d = distanceToLine(pixelPos, p1, p2);
    if (d <= 3.0) {
        // Overwrite color with black for the line region.
        fragColor = vec4(0.0, 0.0, 0.0, 1.0);
    }
}
