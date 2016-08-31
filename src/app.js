'use strict';

var port    = 3000;
var db_name = "parking-hunt-data";

var express       = require('express');
var app           = express();

var jsonParser    = require('body-parser').json;

var router        = require('./Routers/router.js');
var APIrouterV1   = require('./Routers/APIrouterV1.js');

/**
  *   MIDDLEWARE: jsonParser, send correct headers
  */
app.use(jsonParser());
app.use(function(req, res, next) {
  res.header("Access-Control-Allow-Origin", "*");
  res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");

  if(req.method === "OPTIONS") {
    res.header("Access-Control-Allow-Methods", "PUT,POST,DELETE");
    return res.status(200).json({});
  }

  next();
});


/**
  *   DATABASE CONNECTION
  */
var mongoose  = require('mongoose');
mongoose.connect("mongodb://localhost:27017/" + db_name);
var db        = mongoose.connection;

db.on("error", function(err) {
  console.error("db connection error:", err);
});
db.once("open", function() {
  console.log("db connection successful: mongodb://localhost:27017/" + db_name);
});


/**
  *   ROUTES HANDLING
  */

app.use("/api/v1/", APIrouterV1);
app.use(router);


/**
  *   ERROR HANDLING: 404 Error and Internal Server Errors
  */
//404
app.use(function(req, res, next) {
  var err = new Error('Not found');
  err.status = 404;
  next(err);
});
//5XX
app.use(function(err, req, res, next) {
  res.status(err.status || 500);
  res.json({
    error: {
      message : err.message
    }
  });
});

app.listen(port, function() {
  console.log("Server running on http://localhost:3000");
});
