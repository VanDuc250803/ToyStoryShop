const mongoose = require('mongoose');

const ProductSchema = mongoose.Schema({
    //id ngẫu nhiên của mongo không cần khai báo
    owerId: {
        type: String,
        maxlength: 255
    },
    statusPro: {
        type: Boolean
    },
    price: {
        type: Number,
        required: true
    },
    desPro: {
        type: String,
        maxlength: 10000
    },
    creatDatePro: {
        type: Date
    },
    quantity: {
        type: Number,
        required: true
    },
    listPro: {
        type: String,
        maxlength: 255
    },
    imgPro: {
        type: [String],
        maxlength: 255
    },
    namePro: {
        type: String,
        maxlength: 255
    },
    cateId: { 
        type: String,
        maxlength: 255 },

    brand: {
        type: String,
        maxlength: 255
    }
});

const ProductModel = mongoose.model('product', ProductSchema);

module.exports = ProductModel;