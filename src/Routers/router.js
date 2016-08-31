'use strict';

var express = require('express');
var router  = express.Router();

router.get("/", function(req, res) {

  res.json({test: "it works!"});
});


module.exports = router;
