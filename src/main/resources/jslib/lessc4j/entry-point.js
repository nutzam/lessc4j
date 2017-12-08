
var __rootDir = "/";
var __dir = "/lessc4j/";
var __file = "/lessc4j/wendal.js";

// require.js
eval(lessc4j.readJs('/less-nashorn/require.js'));

require("console");
less = require('../less-nashorn/index');
_result.put('less', less);

//less.render(lessStr).then(function (output) {_result.put('css', output.css);});