const mongoose = require('mongoose');

const OrderSchema = mongoose.Schema({
    //id ngẫu nhiên của mongo không cần khai báo
    cusId: {
        type: String,
        maxlength: 255
    },
    revenue_all: {
        type: Number,
        required: true
    },
    name_order: {
        type: String,
        maxlength: 255
    },
    phone_order: {
        type: String,
        maxlength: 255
    },
    address_order: {
        type: String,
        maxlength: 255
    },
    payment_method: {
        type: String,
        maxlength: 255
    },
    prodDetails: [{
        prodId: {
            type: mongoose.Schema.Types.ObjectId, // Dùng ObjectId để liên kết đến sản phẩm
            ref: 'product', // Tham chiếu đến collection 'product'
            required: true
        },
        revenue: {
            type: Number,
            required: true
        },
        quantity: {
            type: Number,
            required: true
        },
        prodSpecification : {
            type: String,
            maxlength: 255
        },
    }],
    content: {
        type: String,
        maxlength: 255
    },
    orderStatus: {
        type: String,
        enum: [
            'Chờ xác nhận',
            'Chờ lấy hàng',
            'Chờ giao hàng',
            'Đã giao',
            'Đã hủy',
            'Hoàn hàng'
        ],
        maxlength: 255,
        required: true
    },
    orderDate: {
        type: Date
    },

});

const OrderModel = mongoose.model('order', OrderSchema);
module.exports = OrderModel;
