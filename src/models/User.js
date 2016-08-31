'use strict';

var mongoose = require('mongoose');
var crypto   = require('crypto');
var bcrypt   = require('bcrypt-nodejs');

var Schema = mongoose.Schema;

var UserSchema = new Schema({
  email: String,
  firstname: String,
  lastname: String,
  credits: Number,                  //experimental
  password: String,
  ApiKey: String,
  roles: {type: [], default: ["user"]},
  createdAt: {type: Date, default: Date.now},
  updatedAt: {type: Date, default: Date.now},
  deletedAt: {type: Date}
});

UserSchema.pre('save', function(next) {

  this.ApiKey = crypto.randomBytes(64).toString('hex');

  this.password = bcrypt.hashSync(this.password);

  next();
});

UserSchema.methods.change = function(body, callback) {

  this.email = body.email;
  this.password = bcrypt.hashSync(body.password);
  this.updatedAt = new Date();

  this.update(this, function(err, user) {

    if(err) callback(err);

    callback();
  });
};

UserSchema.methods.delete = function(callback) {

  this.deletedAt = new Date();
  this.ApiKey = "";
  this.update(this, function(err, user) {

    if(err) callback(err);

    callback();
  });
};

var User = mongoose.model("User", UserSchema);

module.exports.User = User;
