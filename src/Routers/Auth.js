'use strict';

var express     = require('express');
var authRouter  = express.Router();

var User        = require('../models/User.js').User;
var bcrypt      = require('bcrypt-nodejs');

var DEBUG_MODE  = false;             //  SET DEBUG MODE, WILL SWITCH THE AUTHENTICATION OFF

/**
  *   AUTHENTICATION MIDDLEWARE:
  *   - login/register user
  *   - checks if the request is authenticated
  *   - sets the specific user that matches the accID ApiKey pair for other Routers
  *
  */

/**
  *   LOGIN
  */
authRouter.post('/login', function(req, res, next) {

  var email     = req.body.email;
  var password  = req.body.password;

  User.findOne({email: email, deletedAt: null}, function(err, user) {          // get the user

    if(err) next(err);

    if(!user) {

      var err = new Error('Login Error: email does not exists');        // if there is no such user, return error
      err.status = 400;
      return next(err);
    }

    if(bcrypt.compareSync(password, user.password)) {      // compare passwords

      res.json({
        user: {
          accID: user._id,
          ApiKey: user.ApiKey,
          firstname: user.firstname,
          lastname: user.lastname
        }
      });

    } else {

      var err = new Error('Login Error: wrong password');
      err.status = 400;
      return next(err);
    }
  });
});

/**
  *   REGISTER, TODO validate input -> in model?
  */
authRouter.post('/register', function(req, res, next) {

  User.count({email: req.body.email, deletedAt: null}, function(err, count) {

    if(err) next(err);

    if(count > 0) {                                                       // check if email exists

      var err = new Error('Registration Error: E-mail already exists');
      err.status = 400;
      return next(err);

    } else {

      var user = new User(req.body);                                    // create new user
      user.save(function(err, user) {

        if(err) next(err);

        res.status(201);
        res.json({                                                      // return accID and ApiKey
          user: {
            accID:  user._id,
            ApiKey: user.ApiKey
          }
        });
      });
    }
  });
});

/**
  *   AUTHENTICATE: check if request is authenticated and set user to req.user for future routes
  */
authRouter.all('*', function(req, res, next) {

  if(DEBUG_MODE) {

    User.findOne({email: "test@example.com"}, function(err, user) {

      if(err) next(err);

      req.user = user;
      return next();
    });

  } else {

    if(req.body.Credentials === undefined) {
      return authError(next, "No Credentials");
    }
    var accID  = req.body.Credentials.accID === undefined ? "" : req.body.Credentials.accID;
    var ApiKey = req.body.Credentials.ApiKey === undefined ? "" : req.body.Credentials.ApiKey;

    User.findById(accID, function(err, user) {

      if(err) next(err);

      if(!user) {

        return authError(next);
      }

      if(user.ApiKey == ApiKey) {

        req.user = user;
        next();

      } else {

        return authError(next);
      }

    });
  }
});

/**
  *   authError:  creates an Authentication Error
  *   @param next, the next function
  *   @param msg, optional additional error message
  *
  *   @return void
  *   CALL: return authError(next, [msg]);
  */
function authError(next, msg) {

  msg = msg || "Authentication Error: wrong Credentials";
  var err = new Error(msg);
  err.status = 403; //maybe 401 ?
  return next(err);
}

module.exports = authRouter;
