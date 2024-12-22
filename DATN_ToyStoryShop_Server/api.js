const express = require('express');
const router = express.Router();
const mongoose = require('mongoose'); 
const FavoriteModel = require('./model/FavoriteModel');
const FeebackAppModel = require('./model/FeebackAppModel');
const FeebackModel = require('./model/FeebackModel');
const CartModel = require('./model/CartModel');
const productModel = require('./model/productModel');
const OrderModel = require('./model/OrderModel');
const VoucherModel = require('./model/VoucherModel');
const ArtStory = require('./model/ArtStoryModel');
const Address = require('./model/AddressModel');
const Chat = require('./model/ChatModel');
const RefundModel = require('./model/RefunModel');
const server = require('./server');

router.get('/', (req, res) => {
    res.send('URI:' + app.uri);
});


/////////////////AddressModel/////////////////
router.get('/addresses', async (req, res) => {
    try {
        const { cusId } = req.query;
                // Kiểm tra nếu không có cusId được truyền vào
                if (!cusId) {
                    return res.status(400).json({ error: 'cusId là bắt buộc.' });
                }

        // Lấy tất cả các địa chỉ trong database
        const addresses = await Address.find({ cusId }).sort({ isDefault: -1 });
        res.json(addresses);
    } catch (error) {
        console.error('Lỗi khi lấy tất cả địa chỉ:', error);
        res.status(500).json({ error: 'Có lỗi xảy ra khi lấy tất cả địa chỉ.' });
    }
});
router.put('/update-default-address', async (req, res) => {
    try {
        const { cusId } = req.query; // Lấy cusId từ body (hoặc có thể là query string nếu cần)

        // Kiểm tra nếu không có cusId được truyền vào
        if (!cusId) {
            return res.status(400).json({ error: 'cusId là bắt buộc.' });
        }

        // Cập nhật tất cả các địa chỉ có isDefault = true thành false cho cusId tương ứng
        const result = await Address.updateMany(
            { cusId, isDefault: true },  // Điều kiện lọc: cusId và isDefault = true
            { $set: { isDefault: false } }  // Cập nhật isDefault thành false
        );

        // Kiểm tra xem có địa chỉ nào được cập nhật không
        if (result.modifiedCount > 0) {
            res.json({ message: `Cập nhật thành công ${result.modifiedCount} địa chỉ.` });
        } else {
            res.json({ message: 'Không tìm thấy địa chỉ nào với isDefault = true.' });
        }
    } catch (error) {
        console.error('Lỗi khi cập nhật địa chỉ:', error);
        res.status(500).json({ error: 'Có lỗi xảy ra khi cập nhật địa chỉ.' });
    }
});

router.get('/addresses/:id', async (req, res) => {
    try {
        const addressId = req.params.id;

        // Tìm địa chỉ theo ID
        const address = await Address.findById(addressId);

        // Kiểm tra nếu không tìm thấy địa chỉ
        if (!address) {
            return res.status(404).json({ message: "Address not found" });
        }

        // Trả về địa chỉ tìm thấy
        res.status(200).json(address);
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: "Server error" });
    }
});

router.post('/add/addresses', async (req, res) => {
    try {
        const { cusId, name, phone, address, addressDetail, isDefault } = req.body;

        // Tạo đối tượng Address mới
        const newAddress = new Address({
            cusId, // Giá trị mặc định tạm thời
            name,
            phone,
            address,
            addressDetail,
            isDefault: isDefault || false, // Mặc định là `false` nếu không được truyền
        });

        // Lưu địa chỉ vào cơ sở dữ liệu
        const savedAddress = await newAddress.save();
        res.status(201).json({
            success: true,
            message: "Địa chỉ đã được thêm thành công",
            data: savedAddress,
        });
    } catch (error) {
        console.error("Lỗi khi thêm địa chỉ:", error);
        res.status(500).json({
            success: false,
            message: "Không thể thêm địa chỉ",
            error: error.message,
        });
    }
});

router.put('/update/addresses/:id', async (req, res) => {
    try {
        const addressId = req.params.id; // Lấy `id` từ URL
        const { name, phone, address, addressDetail,isDefault } = req.body; // Lấy các trường khác (loại bỏ `isDefault`)

        // Tìm và cập nhật địa chỉ theo ID (bỏ qua isDefault)
        const updatedAddress = await Address.findByIdAndUpdate(
            addressId, // ID cần cập nhật
            {
                name,
                phone,
                address,
                addressDetail,
                isDefault
            }, // Chỉ cập nhật các trường này
            { new: true, runValidators: true } // Tùy chọn trả về bản ghi sau khi cập nhật và kiểm tra ràng buộc
        );

        // Kiểm tra nếu không tìm thấy địa chỉ
        if (!updatedAddress) {
            return res.status(404).json({ success: false, message: "Address not found" });
        }

        // Trả về địa chỉ sau khi cập nhật
        res.status(200).json({
            success: true,
            message: "Địa chỉ đã được cập nhật thành công",
            data: updatedAddress,
        });
    } catch (error) {
        console.error("Lỗi khi cập nhật địa chỉ:", error);
        res.status(500).json({
            success: false,
            message: "Không thể cập nhật địa chỉ",
            error: error.message,
        });
    }
});

router.delete('/deleteAddresses/:id', async (req, res) => {
    try {
        const addressId = req.params.id; // Lấy ID từ tham số URL

        // Tìm và xóa địa chỉ theo ID
        const deletedAddress = await Address.findByIdAndDelete(addressId);

        // Kiểm tra nếu không tìm thấy địa chỉ
        if (!deletedAddress) {
            return res.status(404).json({
                success: false,
                message: "Không tìm thấy địa chỉ để xóa",
            });
        }

        // Trả về thông báo thành công sau khi xóa
        res.status(200).json({
            success: true,
            message: "Địa chỉ đã được xóa thành công",
            data: deletedAddress,
        });
    } catch (error) {
        console.error("Lỗi khi xóa địa chỉ:", error);
        res.status(500).json({
            success: false,
            message: "Không thể xóa địa chỉ",
            error: error.message,
        });
    }
});


/////////////////ArtStoryModel/////////////////
router.post('/artstories', async (req, res) => {
    try {
        const artStory = new ArtStory(req.body);
        await artStory.save();
        res.status(201).json(artStory);
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
});

router.get('/artstories', async (req, res) => {
    try {
        const artStories = await ArtStory.find();
        res.json(artStories);
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
});

router.get('/artstories/:id', async (req, res) => {
    try {
        const artStory = await ArtStory.findById(req.params.id);
        if (!artStory) return res.status(404).json({ message: 'ArtStory not found' });
        res.json(artStory);
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
});

router.put('/artstories/:id', async (req, res) => {
    try {
        const artStory = await ArtStory.findByIdAndUpdate(req.params.id, req.body, { new: true });
        if (!artStory) return res.status(404).json({ message: 'ArtStory not found' });
        res.json(artStory);
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
});

router.delete('/artstories/:id', async (req, res) => {
    try {
        const artStory = await ArtStory.findByIdAndDelete(req.params.id);
        if (!artStory) return res.status(404).json({ message: 'ArtStory not found' });
        res.json({ message: 'ArtStory deleted' });
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
});


/////////////////CartModel/////////////////
router.get('/carts', async (req, res) => {
    try {
        const { cusId } = req.query;

        if (!cusId) {
            return res.status(400).json({ error: 'cusId không được để trống.' });
        }
        console.log('cusId truyền vào:', cusId);
        await mongoose.connect(server.uri);

        // Tìm tất cả các sản phẩm trong collection 'carts'
        const carts = await CartModel.find({ cusId }, '_id prodId quantity cusId prodSpecification ');
        console.log('Kết quả truy vấn:', carts);
        if (carts.length === 0) {
            return res.status(404).json({ error: 'Không có sản phẩm nào trong giỏ hàng.' });
        }

        res.json(carts);
    } catch (error) {
        console.error('Lỗi khi lấy sản phẩm trong giỏ hàng.', error);
        res.status(500).json({ error: 'Có lỗi xảy ra khi lấy sản phẩm trong giỏ hàng.' });
    }
});
// router.delete('/deleteCartItem', async (req, res) => {
//     try {
//         const { prodId, cusId } = req.body; // Lấy prodId và cusId từ body

//         // Tìm và xóa sản phẩm theo prodId và cusId
//         const deletedCartItem = await CartModel.findOneAndDelete({ prodId, cusId });

//         // Kiểm tra nếu không tìm thấy sản phẩm trong giỏ hàng
//         if (!deletedCartItem) {
//             return res.status(404).json({
//                 success: false,
//                 message: "Không tìm thấy sản phẩm để xóa trong giỏ hàng",
//             });
//         }

//         // Trả về thông báo thành công sau khi xóa
//         res.status(200).json({
//             success: true,
//             message: "Sản phẩm đã được xóa thành công khỏi giỏ hàng",
//             data: deletedCartItem,
//         });
//     } catch (error) {
//         console.error("Lỗi khi xóa sản phẩm trong giỏ hàng:", error);
//         res.status(500).json({
//             success: false,
//             message: "Không thể xóa sản phẩm trong giỏ hàng",
//             error: error.message,
//         });
//     }
// });

router.delete('/deleteCart/:id', async (req, res) => {
    const { id } = req.params; // Nhận _id từ đường dẫn

    try {
        await mongoose.connect(server.uri);

        // Xóa sản phẩm trong collection 'Carts' dựa trên _id
        const result = await CartModel.deleteOne({ prodId: id });
        if (result.deletedCount === 0) {
            return res.status(404).json({ error: 'Không tìm thấy sản phẩm trong giỏ hàng với ID đã cho.' });
        }

        res.status(200).json({ message: 'Sản phẩm trong giỏ hàng đã được xóa thành công.' });
    } catch (error) {
        console.error('Lỗi khi xóa sản phẩm trong giỏ hàng:', error);
        res.status(500).json({ error: 'Có lỗi xảy ra khi xóa sản phẩm trong giỏ hàng.' });
    }
});

router.delete('/deleteCartId/:cartId', async (req, res) => {
    const { id } = req.params; // Nhận _id từ đường dẫn

    try {
        await mongoose.connect(server.uri);

        // Xóa sản phẩm trong collection 'Carts' dựa trên _id
        const result = await CartModel.deleteOne({ cartId: id });
        if (result.deletedCount === 0) {
            return res.status(404).json({ error: 'Không tìm thấy sản phẩm trong giỏ hàng với ID đã cho.' });
        }

        res.status(200).json({ message: 'Sản phẩm trong giỏ hàng đã được xóa thành công.' });
    } catch (error) {
        console.error('Lỗi khi xóa sản phẩm trong giỏ hàng:', error);
        res.status(500).json({ error: 'Có lỗi xảy ra khi xóa sản phẩm trong giỏ hàng.' });
    }
});

router.post('/add/add-to-cart', async (req, res) => {
    console.log(req.body); // Xem dữ liệu nhận được trong body

    try {
        const { prodId, quantity, cusId, prodSpecification } = req.body;
        const newCart = new CartModel({ prodId, quantity, cusId, prodSpecification });
        await newCart.save();
        res.status(201).json({ message: 'Thêm vào giỏ hàng thành công!', data: newCart });
    } catch (error) {
        console.error('Lỗi chi tiết:', error);
        res.status(500).json({ message: 'Lỗi khi thêm vào giỏ hàng', error });
    }
});

router.put('/update/cart/:cartId', async (req, res) => {
    console.log(req.body); // Xem dữ liệu nhận được trong body

    try {
        const { cartId } = req.params; // Lấy id từ URL
        const { quantity, prodSpecification } = req.body; // Lấy các thông tin cập nhật từ body

        // Tìm sản phẩm theo id
        const cartItem = await CartModel.findById({ _id: cartId });
        if (!cartItem) {
            return res.status(404).json({ message: 'Sản phẩm không tồn tại trong giỏ hàng!' });
        }

        // Cập nhật các trường
        if (quantity !== undefined) cartItem.quantity = quantity;
        if (prodSpecification !== undefined) cartItem.prodSpecification = prodSpecification;

        await cartItem.save(); // Lưu cập nhật

        res.status(200).json({ message: 'Cập nhật sản phẩm trong giỏ hàng thành công!', data: cartItem });
    } catch (error) {
        console.error('Lỗi chi tiết:', error);
        res.status(500).json({ message: 'Lỗi khi cập nhật sản phẩm trong giỏ hàng', error });
    }
});

router.get('/cart/check-product', async (req, res) => {
    const { prodId, cusId } = req.query; // Lấy productId và customerId từ query

    try {
        // Kết nối đến MongoDB
        await mongoose.connect(server.uri);

        // Tìm sản phẩm trong giỏ hàng
        const cartItem = await CartModel.findOne({ prodId, cusId });

        if (!cartItem) {
            return res.json({ exists: false }); // Sản phẩm không tồn tại trong giỏ hàng
        }

        res.json({ exists: true }); // Sản phẩm đã tồn tại trong giỏ hàng
    } catch (error) {
        console.error('Lỗi khi kiểm tra sản phẩm:', error);
        res.status(500).json({ error: 'Có lỗi xảy ra khi kiểm tra sản phẩm.', details: error.message });
    }
});


/////////////////ChatModel/////////////////
router.get('/chat/history', async (req, res) => {
    try {
        const { cusId } = req.query;

        if (!cusId) {
            return res.status(400).json({ error: 'Thiếu thông tin người dùng!' });
        }

        const chatHistory = await Chat.find({
            $or: [
                { cusId, userId: { $ne: cusId } },
                { cusId: { $ne: cusId }, userId: cusId },
            ],
        }).sort({ timestamp: 1 });

        if (!chatHistory.length) {
            return res.status(404).json({ error: 'Không tìm thấy lịch sử chat!' });
        }

        res.status(200).json({ data: chatHistory });
    } catch (error) {
        res.status(500).json({ error: 'Lỗi khi lấy lịch sử tin nhắn', details: error.message });
    }
});

router.post('/chat/send', async (req, res) => {
    try {
        const { cusId, userId, message, chatType } = req.body;

        if (!cusId || !message || !chatType) {
            return res.status(400).json({ error: 'Thiếu thông tin cần thiết!' });
        }
        const newMessage = new Chat({
            cusId,
            userId,
            message,
            chatType,
            timestamp: Date.now(),
        });

        const savedMessage = await newMessage.save();
        res.status(200).json({ message: 'Gửi tin nhắn thành công!', data: savedMessage });
    } catch (error) {
        res.status(500).json({ error: 'Lỗi khi gửi tin nhắn', details: error.message });
    }
});


/////////////////FavoriteModel/////////////////
router.get('/favorites', async (req, res) => {
    try {
        const { cusId } = req.query;

        if (!cusId) {
            return res.status(400).json({ error: 'cusId không được để trống.' });
        }

        // Tìm tất cả các sản phẩm trong collection 'favorites'
        const favorites = await FavoriteModel.find({ cusId }, '_id prodId cusId');

        if (favorites.length === 0) {
            return res.status(404).json({ error: 'Không có sản phẩm yêu thích nào.' });
        }

        res.json(favorites);
    } catch (error) {
        console.error('Lỗi khi lấy sản phẩm yêu thích:', error);
        res.status(500).json({ error: 'Có lỗi xảy ra khi lấy sản phẩm yêu thích.' });
    }
});

router.delete('/deleteFavorite/:id', async (req, res) => {
    const { id } = req.params;
    try {

        const result = await FavoriteModel.deleteOne({ prodId: id });
        if (result.deletedCount === 0) {
            return res.status(404).json({ error: 'Không tìm thấy sản phẩm yêu thích với ID đã cho.' });
        }

        res.status(200).json({ message: 'Sản phẩm yêu thích đã được xóa thành công.' });
    } catch (error) {
        console.error('Lỗi khi xóa sản phẩm yêu thích:', error);
        res.status(500).json({ error: 'Có lỗi xảy ra khi xóa sản phẩm yêu thích.' });
    }
});

router.get('/check-favorite/:prodId', async (req, res) => {
    try {
        const { prodId } = req.params;

        // Kiểm tra xem prodId có tồn tại trong collection 'favorites' không
        const favorite = await FavoriteModel.findOne({ prodId: prodId });

        if (favorite) {
            res.json({ exists: true });
        } else {
            res.json({ exists: false });
        }
    } catch (error) {
        console.error('Lỗi khi kiểm tra sản phẩm yêu thích:', error);
        res.status(500).json({ error: 'Có lỗi xảy ra khi kiểm tra sản phẩm yêu thích.' });
    }
});

router.post('/add/add-to-favorites', async (req, res) => {
    try {
        const { prodId, cusId } = req.body;
        const newFavorite = new FavoriteModel({ prodId, cusId });
        await newFavorite.save();
        res.status(201).json({ message: 'Thêm vào yêu thích thành công!', data: newFavorite });
    } catch (error) {
        console.error('Lỗi chi tiết:', error);
        res.status(500).json({ message: 'Lỗi khi thêm vào yêu thích', error });
    }
});


/////////////////FeebackAppModel/////////////////
router.post('/add/add-to-app-feeback', async (req, res) => {
    console.log(req.body); // Xem dữ liệu nhận được trong body

    try {
        const { cusId, start, content, dateFeed } = req.body;
        const newFeebackApp = new FeebackAppModel({ cusId, start, content, dateFeed });
        await newFeebackApp.save();
        res.status(201).json({ message: 'Thêm vào đáng giá thành công!', data: newFeebackApp });
    } catch (error) {
        console.error('Lỗi chi tiết:', error);
        res.status(500).json({ message: 'Lỗi khi thêm vào đáng giá', error });
    }
});


/////////////////FeebackModel/////////////////
router.get('/feebacks', async (req, res) => {
    try {
        const { prodId } = req.query;

        if (!prodId) {
            return res.status(400).json({ error: 'prodId không được để trống.' });
        }

        await mongoose.connect(server.uri);

        // Tìm các đánh giá trong FeebackModel theo prodId
        const feebacks = await FeebackModel.find(
            { prodId }, // Lọc theo prodId
            '_id cusId prodId start content dateFeed' // Lấy các trường cần thiết
        );

        if (feebacks.length === 0) {
            return res.status(404).json({ error: 'Không có đánh giá cho sản phẩm này.' });
        }

        res.json(feebacks);
    } catch (error) {
        console.error('Lỗi khi lấy đánh giá.', error);
        res.status(500).json({ error: 'Có lỗi xảy ra khi lấy đánh giá.' });
    }
});

router.get('/average-rating/:prodId', async (req, res) => {
    try {
        const { prodId } = req.params;

        // Lấy tất cả feedback của sản phẩm theo prodId
        const feedbacks = await FeebackModel.find({ prodId });

        if (feedbacks.length === 0) {
            return res.status(404).json({
                message: 'No feedback found for this product.',
                averageRating: 0
            });
        }

        // Tính số sao trung bình
        const totalStars = feedbacks.reduce((sum, feedback) => sum + feedback.start, 0);
        const averageRating = Math.max(1, Math.min(5, totalStars / feedbacks.length)); // Đảm bảo trong khoảng [1, 5]

        res.status(200).json({
            prodId,
            averageRating: averageRating.toFixed(2), // Làm tròn 2 chữ số thập phân
            totalFeedbacks: feedbacks.length
        });
    } catch (error) {
        res.status(500).json({
            message: 'Internal server error',
            error: error.message
        });
    }
});

router.get('/check-feedback', async (req, res) => {
    try {
        const { cusId, prodId } = req.query;

        // Kiểm tra tham số đầu vào
        if (!cusId || !prodId) {
            return res.status(400).json({ message: 'Thiếu tham số cusId hoặc prodId' });
        }

        await mongoose.connect(server.uri);

        // Tìm xem đã có đánh giá cho prodId và cusId này chưa
        const feedback = await FeebackModel.findOne({ cusId, prodId });

        if (feedback) {
            return res.status(200).json({
                feedback: true,
                message: 'Bạn đã đánh giá sản phẩm này.',
                data: feedback,
            });
        } else {
            return res.status(200).json({
                feedback: false,
                message: 'Bạn chưa đánh giá sản phẩm này.',
            });
        }
    } catch (error) {
        console.error('Lỗi kiểm tra đánh giá:', error);
        res.status(500).json({ message: 'Lỗi server.' });
    }
});

router.post('/add-feedback', async (req, res) => {
    console.log(req.body);  // Ghi log dữ liệu nhận được

    try {
        // Lấy dữ liệu từ request body
        const { cusId, prodId, start, content, dateFeed } = req.body;

        // Tạo feedback mới
        const newFeedback = new FeebackModel({ cusId, prodId, start, content, dateFeed });

        // Lưu vào cơ sở dữ liệu
        await newFeedback.save();

        // Trả về phản hồi thành công
        res.status(201).json({ message: 'Đánh giá đã được thêm thành công.', data: newFeedback });
    } catch (error) {
        console.error("Error:", error); // Ghi log lỗi chi tiết
        res.status(500).json({ message: 'Có lỗi xảy ra.', error });
    }
});


/////////////////OrderModel/////////////////
router.get('/all-product-details', async (req, res) => {
    const { cusId } = req.query; // Lấy cusId từ query parameter

    if (!cusId) {
        return res.status(400).json({ error: "cusId is required" });
    }

    try {
        const orders = await OrderModel.aggregate([
            // Lọc theo cusId trong bảng Order
            {
                $match: { cusId: cusId,
                    orderStatus: { $in: ['Hoàn hàng', 'Đã giao'] } // Điều kiện lọc trạng thái đơn hàng
                 }
            },
            { 
                $unwind: "$prodDetails"  // Tách từng sản phẩm trong danh sách prodDetails
            },
            { 
                $lookup: {
                    from: "products",  // Tham chiếu tới collection "product"
                    localField: "prodDetails.prodId",  // Trường trong bảng order
                    foreignField: "_id",  // Trường trong bảng product
                    as: "productInfo"  // Tên trường mới lưu thông tin sản phẩm
                }
            },
            { 
                $unwind: "$productInfo"  // Tách thông tin sản phẩm
            },
            // Tìm phản hồi trong bảng "feebacks" cho mỗi sản phẩm
            { 
                $lookup: {
                    from: "feebacks",  // Tham chiếu tới collection "feedback"
                    localField: "prodDetails.prodId",  // Trường prodId từ bảng order
                    foreignField: "prodId",  // Trường prodId từ bảng feedback
                    as: "feedbackInfo"  // Tên trường lưu thông tin feedback
                }
            },
            // Kiểm tra chỉ lấy các sản phẩm chưa có phản hồi từ cusId trong bảng "feedback"
            { 
                $match: {
                    "feedbackInfo": { $not: { $elemMatch: { cusId: cusId } } } // Lọc các sản phẩm chưa có phản hồi từ cusId này
                }
            },
            { 
                $lookup: {
                    from: "feebacks",  // Lọc lại feedback để đảm bảo cusId không trùng với trong bảng feedback
                    localField: "prodDetails.prodId",  // Trường prodId từ bảng order
                    foreignField: "prodId",  // Trường prodId từ bảng feedback
                    as: "feedbackInfoCheck"  // Lưu thông tin feedback để kiểm tra
                }
            },
            { 
                $match: {
                    "feedbackInfoCheck.cusId": { $ne: cusId }  // Lọc những sản phẩm mà cusId trong feedback không trùng với cusId trong query
                }
            },
            { 
                $project: {
                    prodId: "$prodDetails.prodId",
                    quantity: "$prodDetails.quantity",
                    revenue: "$prodDetails.revenue",
                    namePro: "$productInfo.namePro",
                    imgPro: "$productInfo.imgPro"
                }
            }
        ]);

        // Trả về kết quả
        res.status(200).json(orders);

    } catch (error) {
        console.error(error);
        res.status(500).json({ error: 'Something went wrong' });
    }
});

router.get('/orders/confirm', async (req, res) => {
    try {
        const { cusId } = req.query;

        if (!cusId) {
            return res.status(400).json({ error: 'cusId không được để trống.' });
        }

        await mongoose.connect(server.uri);

        // Tìm tất cả các sản phẩm trong collection 'orders'
        const orders = await OrderModel.find({ cusId, orderStatus: 'Chờ xác nhận' }, '_id cusId revenue_all name_order phone_order address_order payment_method prodDetails content orderStatus orderDate ');

        if (orders.length === 0) {
            return res.status(404).json({ error: 'Không có sản phẩm nào trong đơn hàng.' });
        }

        res.json(orders);
    } catch (error) {
        console.error('Lỗi khi lấy sản phẩm trong đơn hàng.', error);
        res.status(500).json({ error: 'Có lỗi xảy ra khi lấy sản phẩm trong đơn hàng.' });
    }
});

router.get('/orders/getgoods', async (req, res) => {
    try {
        const { cusId } = req.query;

        if (!cusId) {
            return res.status(400).json({ error: 'cusId không được để trống.' });
        }

        await mongoose.connect(server.uri);

        // Tìm tất cả các sản phẩm trong collection 'orders'
        const orders = await OrderModel.find({ cusId, orderStatus: 'Chờ lấy hàng' }, '_id cusId revenue_all name_order phone_order address_order payment_method prodDetails content orderStatus orderDate ');

        if (orders.length === 0) {
            return res.status(404).json({ error: 'Không có sản phẩm nào trong đơn hàng.' });
        }

        res.json(orders);
    } catch (error) {
        console.error('Lỗi khi lấy sản phẩm trong đơn hàng.', error);
        res.status(500).json({ error: 'Có lỗi xảy ra khi lấy sản phẩm trong đơn hàng.' });
    }
});

router.get('/orders/delivery', async (req, res) => {
    try {
        const { cusId } = req.query;

        if (!cusId) {
            return res.status(400).json({ error: 'cusId không được để trống.' });
        }

        await mongoose.connect(server.uri);

        // Tìm tất cả các sản phẩm trong collection 'orders'
        const orders = await OrderModel.find({ cusId, orderStatus: 'Chờ giao hàng' }, '_id cusId revenue_all name_order phone_order address_order payment_method prodDetails content orderStatus orderDate ');

        if (orders.length === 0) {
            return res.status(404).json({ error: 'Không có sản phẩm nào trong đơn hàng.' });
        }

        res.json(orders);
    } catch (error) {
        console.error('Lỗi khi lấy sản phẩm trong đơn hàng.', error);
        res.status(500).json({ error: 'Có lỗi xảy ra khi lấy sản phẩm trong đơn hàng.' });
    }
});

router.get('/orders/successful', async (req, res) => {
    try {
        const { cusId } = req.query;

        if (!cusId) {
            return res.status(400).json({ error: 'cusId không được để trống.' });
        }

        await mongoose.connect(server.uri);

        // Tìm tất cả các sản phẩm trong collection 'orders'
        const orders = await OrderModel.find({ cusId, orderStatus: 'Đã giao' }, '_id cusId revenue_all name_order phone_order address_order payment_method prodDetails content orderStatus orderDate ');

        if (orders.length === 0) {
            return res.status(404).json({ error: 'Không có sản phẩm nào trong đơn hàng.' });
        }

        res.json(orders);
    } catch (error) {
        console.error('Lỗi khi lấy sản phẩm trong đơn hàng.', error);
        res.status(500).json({ error: 'Có lỗi xảy ra khi lấy sản phẩm trong đơn hàng.' });
    }
});

router.get('/orders/canceled', async (req, res) => {
    try {
        const { cusId } = req.query;

        if (!cusId) {
            return res.status(400).json({ error: 'cusId không được để trống.' });
        }

        await mongoose.connect(server.uri);

        // Tìm tất cả các sản phẩm trong collection 'orders'
        const orders = await OrderModel.find({ cusId, orderStatus: 'Đã hủy' }, '_id cusId revenue_all name_order phone_order address_order payment_method prodDetails content orderStatus orderDate ');

        if (orders.length === 0) {
            return res.status(404).json({ error: 'Không có sản phẩm nào trong đơn hàng.' });
        }

        res.json(orders);
    } catch (error) {
        console.error('Lỗi khi lấy sản phẩm trong đơn hàng.', error);
        res.status(500).json({ error: 'Có lỗi xảy ra khi lấy sản phẩm trong đơn hàng.' });
    }
});

router.put('/update/order/:orderId', async (req, res) => {
    console.log(req.body); // Xem dữ liệu nhận được trong body

    try {
        const { orderId } = req.params; // Lấy id từ URL
        const { orderStatus } = req.body; // Lấy các thông tin cập nhật từ body

        // Tìm sản phẩm theo id
        const orderItem = await OrderModel.findById({ _id: orderId });
        if (!orderItem) {
            return res.status(404).json({ message: 'Sản phẩm không tồn tại trong order!' });
        }

        // Cập nhật các trường
        if (orderStatus !== undefined) orderItem.orderStatus = orderStatus;
    

        await orderItem.save(); // Lưu cập nhật

        res.status(200).json({ message: 'Cập nhật sản phẩm trong order thành công!', data: orderItem });
    } catch (error) {
        console.error('Lỗi chi tiết:', error);
        res.status(500).json({ message: 'Lỗi khi cập nhật sản phẩm trong order', error });
    }
});
// router.put('/update/refund/:orderId', async (req, res) => {
//     console.log(req.body); // Xem dữ liệu nhận được trong body

//     try {
//         const { orderId } = req.params; // Lấy id từ URL
//         const { refundStatus } = req.body; // Lấy các thông tin cập nhật từ body

//         // Tìm sản phẩm theo orderId
//         const refundItem = await RefundModel.findOne({ orderId });
//         if (!refundItem) {
//             return res.status(404).json({ message: 'Sản phẩm không tồn tại trong refund!' });
//         }

//         // Cập nhật các trường
//         if (refundStatus !== undefined) refundItem.refundStatus = refundStatus;
    

//         await refundItem.save(); // Lưu cập nhật

//         res.status(200).json({ message: 'Cập nhật sản phẩm trong refund thành công!', data: refundItem });
//     } catch (error) {
//         console.error('Lỗi chi tiết:', error);
//         res.status(500).json({ message: 'Lỗi khi cập nhật sản phẩm trong refund', error });
//     }
// });
router.put('/update/refund/:_id', async (req, res) => {
    console.log(req.body); // Xem dữ liệu nhận được trong body

    try {
        const { _id } = req.params; // Lấy _id từ URL
        const { refundStatus } = req.body; // Lấy các thông tin cập nhật từ body

        // Tìm sản phẩm theo _id
        const refundItem = await RefundModel.findById(_id);
        if (!refundItem) {
            return res.status(404).json({ message: 'Sản phẩm không tồn tại trong refund!' });
        }

        // Cập nhật các trường
        if (refundStatus !== undefined) refundItem.refundStatus = refundStatus;

        await refundItem.save(); // Lưu cập nhật

        res.status(200).json({ message: 'Cập nhật sản phẩm trong refund thành công!', data: refundItem });
    } catch (error) {
        console.error('Lỗi chi tiết:', error);
        res.status(500).json({ message: 'Lỗi khi cập nhật sản phẩm trong refund', error });
    }
});

router.post('/add/add-to-order', async (req, res) => {
    console.log(req.body); // Xem dữ liệu nhận được trong body

    try {
        const { cusId, revenue_all, name_order, phone_order, address_order, payment_method, prodDetails, content, orderStatus, orderDate } = req.body;

        // Kiểm tra prodDetails có phải là mảng không
        if (!Array.isArray(prodDetails)) {
            return res.status(400).json({ message: 'prodDetails phải là một mảng!' });
        }

        // Kiểm tra từng phần tử trong prodDetails
        for (let item of prodDetails) {
            if (!item.prodId || !item.revenue || !item.quantity || !item.prodSpecification) {
                return res.status(400).json({ message: 'Mỗi sản phẩm phải có prodId và revenue!' });
            }
        }

        const newOrder = new OrderModel({ cusId, revenue_all, name_order, phone_order, address_order, payment_method, prodDetails, content, orderStatus, orderDate });
        await newOrder.save();
        res.status(201).json({
            message: 'Thêm vào lịch sử mua thành công!',
            orderId: newOrder._id, // Trả về _id để client sử dụng
            data: newOrder
        });
        } catch (error) {
        console.error('Lỗi chi tiết:', error);
        res.status(500).json({ message: 'Lỗi khi thêm vào lịch sử mua', error });
    }
});


/////////////////ProductModel/////////////////
router.get('/list', async (req, res) => {
    await server.mongoose.connect(server.uri);

    try {
        let products = await server.productModel.find(); // Lấy tất cả sản phẩm
        console.log(products);
        res.send(products);
    } catch (error) {
        console.error("Lỗi khi lấy sản phẩm:", error);
        res.status(500).send({ error: "Có lỗi xảy ra khi lấy sản phẩm." });
    }
});

router.get('/brand-counts', async (req, res) => {
    try {
        await mongoose.connect(server.uri);

        // Các thương hiệu bạn muốn đếm
        const brands = ["BANPRESTO", "POP MART", "FUNISM","FUNKO","DZNR","YOLOPARK"];

        const counts = await Promise.all(
            brands.map(async (brand) => {
                // Sử dụng $regex để tìm kiếm không phân biệt dấu cách và dấu câu
                const count = await server.productModel.countDocuments({
                    brand: { $regex: brand.trim(), $options: 'i' } // Sử dụng brand.trim() để loại bỏ khoảng trắng
                });

                return { brand, count };
            })
        );

        res.json(counts);
    } catch (error) {
        console.error("Lỗi khi lấy số lượng sản phẩm theo thương hiệu:", error);
        res.status(500).json({ error: "Có lỗi xảy ra khi lấy số lượng sản phẩm." });
    }
});

router.get('/sale', async (req, res) => {
    try {
        await mongoose.connect(server.uri);
        const newProducts = await server.productModel.find({ listPro: { $regex: "^SALE$", $options: "i" } });

        if (newProducts.length === 0) {
            return res.status(404).json({ error: 'Không tìm thấy sản phẩm giảm nhập.' });
        }

        // Xử lý tên sản phẩm không dấu
        newProducts.forEach(product => {
            product.namePro = removeDiacritics(product.namePro);
        });

        res.json(newProducts);
    } catch (error) {
        console.error('Lỗi khi lấy sản phẩm:', error);
        res.status(500).json({ error: 'Có lỗi xảy ra khi lấy sản phẩm.', details: error.message });
    }
});

router.get('/new-arrivals', async (req, res) => {
    try {
        await mongoose.connect(server.uri);
        const newProducts = await server.productModel.find({ listPro: { $regex: "^NEW ARRIVALS$", $options: "i" } });

        if (newProducts.length === 0) {
            return res.status(404).json({ error: 'Không tìm thấy sản phẩm mới nhập.' });
        }

        // Xử lý tên sản phẩm không dấu
        newProducts.forEach(product => {
            product.namePro = removeDiacritics(product.namePro);
        });

        res.json(newProducts);
    } catch (error) {
        console.error('Lỗi khi lấy sản phẩm:', error);
        res.status(500).json({ error: 'Có lỗi xảy ra khi lấy sản phẩm.', details: error.message });
    }
});

router.get('/limited', async (req, res) => {
    try {
        await mongoose.connect(server.uri);
        const newProducts = await server.productModel.find({ listPro: { $regex: "^LIMITED FIGURE$", $options: "i" } });

        if (newProducts.length === 0) {
            return res.status(404).json({ error: 'Không tìm thấy sản phẩm mới nhập.' });
        }

        newProducts.forEach(product => {
            product.namePro = removeDiacritics(product.namePro);
        });

        res.json(newProducts);
    } catch (error) {
        console.error('Lỗi khi lấy sản phẩm:', error);
        res.status(500).json({ error: 'Có lỗi xảy ra khi lấy sản phẩm.', details: error.message });
    }
});

router.get('/figuring', async (req, res) => {
    try {
        await mongoose.connect(server.uri);
        const newProducts = await server.productModel.find({ listPro: { $regex: "^FIGURING$", $options: "i" } });

        if (newProducts.length === 0) {
            return res.status(404).json({ error: 'Không tìm thấy sản phẩm mới nhập.' });
        }

        newProducts.forEach(product => {
            product.namePro = removeDiacritics(product.namePro);
        });

        res.json(newProducts);
    } catch (error) {
        console.error('Lỗi khi lấy sản phẩm:', error);
        res.status(500).json({ error: 'Có lỗi xảy ra khi lấy sản phẩm.', details: error.message });
    }
});

router.get('/other', async (req, res) => {
    try {
        await mongoose.connect(server.uri);
        const newProducts = await server.productModel.find({ listPro: { $regex: "^OTHER PRODUCTS$", $options: "i" } });

        if (newProducts.length === 0) {
            return res.status(404).json({ error: 'Không tìm thấy sản phẩm mới nhập.' });
        }

        newProducts.forEach(product => {
            product.namePro = removeDiacritics(product.namePro);
        });

        res.json(newProducts);
    } catch (error) {
        console.error('Lỗi khi lấy sản phẩm:', error);
        res.status(500).json({ error: 'Có lỗi xảy ra khi lấy sản phẩm.', details: error.message });
    }
});

router.get('/blind_box', async (req, res) => {
    try {
        await mongoose.connect(server.uri);
        const newProducts = await server.productModel.find({
            listPro: { $regex: "^BLIND BOX$", $options: "i" }
        });

        if (newProducts.length === 0) {
            return res.status(404).json({ error: 'Không tìm thấy sản phẩm mới nhập.' });
        }

        newProducts.forEach(product => {
            product.namePro = removeDiacritics(product.namePro); // Đảm bảo thông tin được xử lý đúng
        });

        // Trả về dữ liệu
        res.json(newProducts);
    } catch (error) {
        console.error('Lỗi khi lấy sản phẩm:', error);
        res.status(500).json({ error: 'Có lỗi xảy ra khi lấy sản phẩm.', details: error.message });
    }
});
router.get('/52_toys', async (req, res) => {
    try {
        await mongoose.connect(server.uri);
        const newProducts = await server.productModel.find({
            listPro: { $regex: "^52TOYS$", $options: "i" }
        });

        if (newProducts.length === 0) {
            return res.status(404).json({ error: 'Không tìm thấy sản phẩm mới nhập.' });
        }

        newProducts.forEach(product => {
            product.namePro = removeDiacritics(product.namePro); // Đảm bảo thông tin được xử lý đúng
        });

        // Trả về dữ liệu
        res.json(newProducts);
    } catch (error) {
        console.error('Lỗi khi lấy sản phẩm:', error);
        res.status(500).json({ error: 'Có lỗi xảy ra khi lấy sản phẩm.', details: error.message });
    }
});
router.get('/blokees', async (req, res) => {
    try {
        await mongoose.connect(server.uri);
        const newProducts = await server.productModel.find({
            listPro: { $regex: "^BLOKEES$", $options: "i" }
        });

        if (newProducts.length === 0) {
            return res.status(404).json({ error: 'Không tìm thấy sản phẩm mới nhập.' });
        }

        newProducts.forEach(product => {
            product.namePro = removeDiacritics(product.namePro); // Đảm bảo thông tin được xử lý đúng
        });

        // Trả về dữ liệu
        res.json(newProducts);
    } catch (error) {
        console.error('Lỗi khi lấy sản phẩm:', error);
        res.status(500).json({ error: 'Có lỗi xảy ra khi lấy sản phẩm.', details: error.message });
    }
});
router.get('/heyone', async (req, res) => {
    try {
        await mongoose.connect(server.uri);
        const newProducts = await server.productModel.find({
            listPro: { $regex: "^HEYONE$", $options: "i" }
        });

        if (newProducts.length === 0) {
            return res.status(404).json({ error: 'Không tìm thấy sản phẩm mới nhập.' });
        }

        newProducts.forEach(product => {
            product.namePro = removeDiacritics(product.namePro); // Đảm bảo thông tin được xử lý đúng
        });

        // Trả về dữ liệu
        res.json(newProducts);
    } catch (error) {
        console.error('Lỗi khi lấy sản phẩm:', error);
        res.status(500).json({ error: 'Có lỗi xảy ra khi lấy sản phẩm.', details: error.message });
    }
});
router.get('/squid_game', async (req, res) => {
    try {
        await mongoose.connect(server.uri);
        const newProducts = await server.productModel.find({
            listPro: { $regex: "^SQUID GAME$", $options: "i" }
        });

        if (newProducts.length === 0) {
            return res.status(404).json({ error: 'Không tìm thấy sản phẩm mới nhập.' });
        }

        newProducts.forEach(product => {
            product.namePro = removeDiacritics(product.namePro); // Đảm bảo thông tin được xử lý đúng
        });

        // Trả về dữ liệu
        res.json(newProducts);
    } catch (error) {
        console.error('Lỗi khi lấy sản phẩm:', error);
        res.status(500).json({ error: 'Có lỗi xảy ra khi lấy sản phẩm.', details: error.message });
    }
});
router.get('/bandai_candy', async (req, res) => {
    try {
        await mongoose.connect(server.uri);
        const newProducts = await server.productModel.find({
            listPro: { $regex: "^BANDAI CANDY$", $options: "i" }
        });

        if (newProducts.length === 0) {
            return res.status(404).json({ error: 'Không tìm thấy sản phẩm mới nhập.' });
        }

        newProducts.forEach(product => {
            product.namePro = removeDiacritics(product.namePro); // Đảm bảo thông tin được xử lý đúng
        });

        // Trả về dữ liệu
        res.json(newProducts);
    } catch (error) {
        console.error('Lỗi khi lấy sản phẩm:', error);
        res.status(500).json({ error: 'Có lỗi xảy ra khi lấy sản phẩm.', details: error.message });
    }
});
router.get('/finding_unicorn', async (req, res) => {
    try {
        await mongoose.connect(server.uri);
        const newProducts = await server.productModel.find({
            listPro: { $regex: "^FINDING UNICORN$", $options: "i" }
        });

        if (newProducts.length === 0) {
            return res.status(404).json({ error: 'Không tìm thấy sản phẩm mới nhập.' });
        }

        newProducts.forEach(product => {
            product.namePro = removeDiacritics(product.namePro); // Đảm bảo thông tin được xử lý đúng
        });

        // Trả về dữ liệu
        res.json(newProducts);
    } catch (error) {
        console.error('Lỗi khi lấy sản phẩm:', error);
        res.status(500).json({ error: 'Có lỗi xảy ra khi lấy sản phẩm.', details: error.message });
    }
});

router.get('/list-popular', async (req, res) => {
    try {
        await mongoose.connect(server.uri);

        // Lọc sản phẩm có listPro là BLIND_BOX hoặc FIGURING
        const popularProducts = await server.productModel.find({
            listPro: { $in: ["BLIND_BOX", "FIGURING"] }
        });

        if (popularProducts.length === 0) {
            return res.status(404).json({ error: 'Không tìm thấy sản phẩm phổ biến.' });
        }

        // Xử lý tên sản phẩm không dấu
        popularProducts.forEach(product => {
            product.namePro = removeDiacritics(product.namePro);
        });

        res.json(popularProducts);
    } catch (error) {
        console.error('Lỗi khi lấy sản phẩm phổ biến:', error);
        res.status(500).json({ error: 'Có lỗi xảy ra khi lấy sản phẩm phổ biến.', details: error.message });
    }
});

router.put('/update/product/:prodId', async (req, res) => {
    console.log(req.body); // Log dữ liệu nhận được trong body

    try {
        const { prodId } = req.params; // Lấy ID từ URL
        const { quantity } = req.body; // Lấy thông tin từ body

        // Kiểm tra ID hợp lệ
        if (!mongoose.Types.ObjectId.isValid(prodId)) {
            return res.status(400).json({ message: 'ID sản phẩm không hợp lệ' });
        }

        // Tìm sản phẩm theo ID
        const productItem = await productModel.findById(prodId);
        if (!productItem) {
            return res.status(404).json({ message: 'Sản phẩm không tồn tại' });
        }

        // Kiểm tra và cập nhật trường `quantity`
        if (quantity !== undefined) {
            if (typeof quantity !== 'number' || quantity < 0) {
                return res.status(400).json({ message: 'Số lượng phải là một số hợp lệ và lớn hơn hoặc bằng 0' });
            }
            productItem.quantity = quantity;
        }

        // Lưu sản phẩm
        await productItem.save();

        res.status(200).json({ message: 'Cập nhật sản phẩm thành công!', data: productItem });
    } catch (error) {
        console.error('Lỗi chi tiết:', error);
        res.status(500).json({ message: 'Đã xảy ra lỗi khi cập nhật sản phẩm' });
    }
});


/////////////////RefunModel/////////////////
router.get('/refund', async (req, res) => {
    try {
        const { cusId } = req.query;

        if (!cusId) {
            return res.status(400).json({ error: 'cusId không được để trống.' });
        }
        console.log('cusId truyền vào:', cusId);
        await mongoose.connect(server.uri);

        // Tìm tất cả các sản phẩm trong collection 'refund'
        const refund = await RefundModel.find({ cusId }, '_id orderId cusId content orderRefundDate refundStatus');
        console.log('Kết quả truy vấn:', refund);
        if (refund.length === 0) {
            return res.status(404).json({ error: 'Không có sản phẩm nào trong refund.' });
        }

        res.json(refund);
    } catch (error) {
        console.error('Lỗi khi lấy sản phẩm trong refund.', error);
        res.status(500).json({ error: 'Có lỗi xảy ra khi lấy sản phẩm trong refund.' });
    }
});

router.post('/add-Refund', async (req, res) => {
    console.log(req.body);  // Ghi log dữ liệu nhận được

    try {
        // Lấy dữ liệu từ request body
        const { orderId, cusId, content, orderRefundDate, refundStatus } = req.body;

        // Tạo feedback mới
        const newRefund = new RefundModel({ orderId, cusId, content, orderRefundDate, refundStatus });

        // Lưu vào cơ sở dữ liệu
        await newRefund.save();

        // Trả về phản hồi thành công
        res.status(201).json({ message: 'Refund đã được thêm thành công.', data: newRefund });
    } catch (error) {
        console.error("Error:", error); // Ghi log lỗi chi tiết
        res.status(500).json({ message: 'Có lỗi xảy ra.', error });
    }
});


/////////////////VoucherModel/////////////////
router.get('/vouchers', async (req, res) => {
    try {
        await mongoose.connect(server.uri);

        // Lấy tất cả các voucher từ database và loại bỏ khoảng trắng ở đầu và cuối của trường `type_voucher`
        const vouchers = await VoucherModel.find({}, '_id price_reduced discount_code type_voucher quantity_voucher');

        // Loại bỏ khoảng trắng thừa trong trường `type_voucher` trước khi trả về
        const cleanedVouchers = vouchers.map(voucher => {
            // Trim khoảng trắng ở đầu và cuối của `type_voucher`
            voucher.type_voucher = voucher.type_voucher.trim();
            return voucher;
        });

        if (cleanedVouchers.length === 0) {
            return res.status(404).json({ error: 'Không có mã giảm giá.' });
        }

        res.json(cleanedVouchers);
    } catch (error) {
        console.error('Lỗi khi lấy mã giảm giá.', error);
        res.status(500).json({ error: 'Có lỗi xảy ra khi lấy mã giảm giá.' });
    }
});
router.put('/update/vouchers/:id', async (req, res) => {
    const { id } = req.params;

    try {
        await mongoose.connect(server.uri);

        // Trừ 1 vào quantity_voucher
        const updatedVoucher = await VoucherModel.findByIdAndUpdate(
            id,
            { $inc: { quantity_voucher: -1 } }, // Giảm 1
            { new: true } // Trả về document đã cập nhật
        );

        if (!updatedVoucher) {
            return res.status(404).json({ error: 'Voucher không tồn tại.' });
        }

        if (updatedVoucher.quantity_voucher < 0) {
            return res.status(400).json({ error: 'Không thể trừ thêm, số lượng voucher đã hết.' });
        }

        res.json({
            message: 'Đã cập nhật số lượng voucher.',
            voucher: updatedVoucher
        });
    } catch (error) {
        console.error('Lỗi khi cập nhật số lượng voucher.', error);
        res.status(500).json({ error: 'Có lỗi xảy ra khi cập nhật voucher.' });
    }
});

router.get('/voucher/:discount_code', async (req, res) => {
    try {
        const { discount_code } = req.params; // Lấy discount_code từ URL

        // Tìm voucher dựa trên discount_code
        const voucher = await VoucherModel.findOne({ discount_code });

        if (!voucher) {
            return res.status(404).json({ message: 'Voucher không tồn tại!' });
        }

        return res.status(200).json(voucher);
    } catch (error) {
        console.error(error);
        return res.status(500).json({ message: 'Đã xảy ra lỗi khi lấy voucher.' });
    }
});





/////////////////bắt buộc nó phải ở cuối/////////////////
function removeDiacritics(input) {
    const normalized = input.normalize("NFD");
    return normalized.replace(/[\u0300-\u036f]/g, "");
};

router.get('/product-by/:prodId', async (req, res) => {
    const { prodId } = req.params; // Lấy prodId từ tham số URL

    try {
        // Kết nối đến MongoDB
        await mongoose.connect(server.uri);
        const product = await server.productModel.findOne({ _id: prodId });

        if (!product) {
            return res.status(404).json({ error: 'Không tìm thấy sản phẩm.' });
        }

        // Xử lý tên sản phẩm không dấu
        product.namePro = removeDiacritics(product.namePro);

        res.json(product);
    } catch (error) {
        console.error('Lỗi khi lấy sản phẩm:', error);
        res.status(500).json({ error: 'Có lỗi xảy ra khi lấy sản phẩm.', details: error.message });
    }
});

router.get('/order-by/:orderId', async (req, res) => {
    const { orderId } = req.params; // Lấy orderId từ tham số URL

    try {
        // Kết nối đến MongoDB
        await mongoose.connect(server.uri);
        const order = await OrderModel.findOne({ _id: orderId });

        if (!order) {
            return res.status(404).json({ error: 'Không tìm thấy order.' });
        }

        res.json(order);
    } catch (error) {
        console.error('Lỗi khi lấy order:', error);
        res.status(500).json({ error: 'Có lỗi xảy ra khi lấy order.', details: error.message });
    }
});

router.get('/cart-by/:cartId', async (req, res) => {
    const { cartId } = req.params; // Lấy prodId từ tham số URL

    try {
        // Kết nối đến MongoDB
        await mongoose.connect(server.uri);

        const cart = await CartModel.findOne({ _id: cartId });
        if (!cart) {
            return res.status(404).json({ error: 'Không tìm thấy sản phẩm.' });
        }

        res.json(cart);
    } catch (error) {
        console.error('Lỗi khi lấy sản phẩm:', error);
        res.status(500).json({ error: 'Có lỗi xảy ra khi lấy sản phẩm.', details: error.message });
    }
});

router.get('/refund-by-order/:orderId', async (req, res) => {
    const { orderId } = req.params; // Lấy orderId từ tham số URL

    try {
        const refunds = await RefundModel.find({ orderId: orderId });

        if (!refunds || refunds.length === 0) {
            return res.status(404).json({ error: 'Không tìm thấy thông tin hoàn hàng với orderId này.' });
        }

        res.status(200).json({ message: 'Lấy thông tin thành công.', data: refunds });
    } catch (error) {
        console.error('Lỗi khi lấy thông tin hoàn hàng:', error);
        res.status(500).json({ error: 'Có lỗi xảy ra khi lấy thông tin hoàn hàng.', details: error.message });
    }
});
router.get('/refund-by/:_id', async (req, res) => {
    const { _id } = req.params; // Lấy _id từ tham số URL

    try {
        // Kết nối đến MongoDB
        await mongoose.connect(server.uri);

        // Tìm refund theo _id
        const refund = await RefundModel.findOne({ _id });

        if (!refund) {
            return res.status(404).json({ error: 'Không tìm thấy refund.' });
        }

        res.json(refund);
    } catch (error) {
        console.error('Lỗi khi lấy refund:', error);
        res.status(500).json({ error: 'Có lỗi xảy ra khi lấy refund.', details: error.message });
    }
});

router.get('/cart/get-cart-id', async (req, res) => {
    const { prodId, cusId } = req.query;

    try {
        // Kết nối đến MongoDB
        await mongoose.connect(server.uri);

        // Tìm sản phẩm trong giỏ hàng
        const cartItem = await CartModel.findOne({ prodId, cusId }).select('_id quantity prodSpecification'); // Chỉ lấy trường _id

        if (!cartItem) {
            return res.status(404).json({ message: 'Không tìm thấy sản phẩm trong giỏ hàng.' });
        }

        res.json({
            cartId: cartItem._id,
            prodSpecification: cartItem.prodSpecification,
            quantity: cartItem.quantity,
        }); // Trả về _id của cart
    } catch (error) {
        console.error('Lỗi khi lấy _id và prodSpecification  của sản phẩm:', error);
        res.status(500).json({ error: 'Có lỗi xảy ra khi truy vấn _id của sản phẩm.', details: error.message });
    }
});

module.exports = router;

