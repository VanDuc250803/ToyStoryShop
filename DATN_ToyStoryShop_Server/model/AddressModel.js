const mongoose = require('mongoose');

const addressSchema = new mongoose.Schema({
  cusId: {
    type: String,
    required: true, 
  },
  name: {
    type: String,
    required: true, 
  },
  phone: {
    type: String,
    required: true,
  },
  address: {
    type: String,
    required: true,
  },
  addressDetail: {
    type: String,
    required: true,
  },
  isDefault: {
    type: Boolean,
    default: false, 
  },
}, { timestamps: true });

module.exports = mongoose.model('Address', addressSchema);
