const mongoose = require('mongoose');

const FavoriteSchema = mongoose.Schema({
    //id ngẫu nhiên của mongo không cần khai báo
    prodId: {
        type: mongoose.Schema.Types.ObjectId, // Dùng ObjectId để liên kết đến sản phẩm
        ref: 'product', // Tham chiếu đến collection 'product'
        required: true
    },
    cusId: {
        type: String,
        maxlength: 255
    }
});

const FavoriteModel = mongoose.model('favorite', FavoriteSchema);

module.exports = FavoriteModel;
