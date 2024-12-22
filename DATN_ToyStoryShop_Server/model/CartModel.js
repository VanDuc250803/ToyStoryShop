const mongoose = require('mongoose');

// Khai báo schema cho Cart
const CartSchema = mongoose.Schema({
        //id ngẫu nhiên của mongo không cần khai báo

    prodId: {
        type: mongoose.Schema.Types.ObjectId, // Dùng ObjectId để liên kết đến sản phẩm
        ref: 'product', // Tham chiếu đến collection 'product'
        required: true
    },
    quantity: {
        type: Number,
        required: true
    },
    cusId: {
        type: String,
        maxlength: 255
    },
    prodSpecification : {
        type: String,
        enum: ['Mô hình riêng lẻ', 'Nguyên set 12 hộp'],
        required: true
    },
});

// Khởi tạo model cho Cart
const CartModel = mongoose.model('cart', CartSchema);

module.exports = CartModel;
