
///////////////////////////////////////////////////////////////
//CALCULATE DISTANCE BEETWEEN TWO COLORS
function deltaE( col1,  col2) {
  var result = 0;

  var xyz1 = rgb2xyz(col1);
  var lab1 = xyz2lab(xyz1);
  var xyz2 = rgb2xyz(col2);
  var lab2 = xyz2lab(xyz2);

  var c1 = sqrt(lab1[1]*lab1[1]+lab1[2]*lab1[2]);
  var c2 = sqrt(lab2[1]*lab2[1]+lab2[2]*lab2[2]);
  var dc = c1-c2;
  var dl = lab1[0]-lab2[0];
  var da = lab1[1]-lab2[1];
  var db = lab1[2]-lab2[2];
  var dh = sqrt((da*da)+(db*db)-(dc*dc));
  var first = dl;
  var second =  dc/(1+0.045*c1);
  var third = dh/(1+0.015*c1);
  result = (sqrt( (first*first+second*second+third*third)));

  return result;
}

function rgb2xyz( rgb) {

 var result = [];

  var r = red(rgb)/255;
  var g = green(rgb)/255;
  var b = blue(rgb)/255;

  if (r>0.04045) {
    r = (r+0.055)/1.055;
    r = pow(r, 2.4);
  } else {
    r = r/12.92;
  }
  if (g>0.04045) {
    g = (g+0.055)/1.055;
    g = pow(g, 2.4);
  } else {
    g = g/12.92;
  }
  if (b>0.04045) {
    b = (b+0.055)/1.055;
    b = pow(b, 2.4);
  } else {
    b = b/12.92;
  }

  b *=100;
  r *=100;
  g *=100;

  result[0] =  (r * 0.4124 + g * 0.3576 + b * 0.1805);
  result[1] =  (r * 0.2126 + g * 0.7152 + b * 0.0722);
  result[2] =  (r * 0.0193 + g * 0.1192 + b * 0.9505);

  return result;
}

function xyz2lab( xyz) {

  var result = [];

  var x = xyz[0]/95.047;
  var y = xyz[1]/100;
  var z = xyz[2]/108.883;

  if (x>0.008856) {
    x = pow( x,  0.333);
  } else {
    x = 7.787*x + 16/116;
  }
  if (y>0.008856) {
    y = pow( y,  0.3333);
  } else {
    y = (7.787*y) + (16/116);
  }
  if (z>0.008856) {
    z = pow( z,  0.333);
  } else {
    z = 7.787*z + 16/116;
  }

  result[0]=  (116*y -16);
  result[1]=  (500*(x-y));
  result[2]=  (200*(y-z));

  return result;
}
