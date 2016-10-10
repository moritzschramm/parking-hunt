'use strict';

var mongoose = require("mongoose");

var Schema = mongoose.Schema;

var SpotSchema = new Schema({
  _user_id: Schema.Types.ObjectId,
  lat: Number,
  lng: Number,
  type: String,
  place: String,
  street: String,
  street_number: String,
  amount: Number,
  createdAt: {type: Date, default: Date.now},
  updatedAt: {type: Date, default: Date.now},
  deletedAt: {type: Date}
});

//  SOFT DELETE
SpotSchema.methods.delete = function(callback) {

  this.deletedAt = new Date();
  this.update(this, function(err, spot) {

    if(err) callback(err);

    callback();
  });
}

var Spot = mongoose.model("Spot", SpotSchema);

module.exports.Spot = Spot;
