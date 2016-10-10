'use strict';

var mongoose = require("mongoose");

var Schema = mongoose.Schema;

var SearchSchema = new Schema({
  _user_id: Schema.Types.ObjectId,
  lat: Number,
  lng: Number,
  type: String,
  place: String,
  street: String,
  streetNumber: String,
  searchAmount: {type: Number, default: 1},
  createdAt: {type: Date, default: Date.now},
  updatedAt: {type: Date, default: Date.now}
});

SearchSchema.methods.incSearchAmount = function(callback) {

  this.searchAmount = this.searchAmount + 1;
  this.update(this, function(err, search) {

    if(err) callback(err, null);

    callback(null, search);
  });  
}

var Search = mongoose.model("Search", SearchSchema);

module.exports.Search = Search;
