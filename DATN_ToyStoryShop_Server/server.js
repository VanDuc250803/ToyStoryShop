const express = require('express');
const mongoose = require('mongoose');
const WebSocket = require('ws');
const ChatModel = require('./model/ChatModel');
const bodyParser = require("body-parser");
const api = require('./api');

const OrderModel = require('./model/OrderModel'); 


const app = express();
const port = 3000;
const WS_PORT = 8080; // Cổng cho WebSocket server

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

app.use ('/api', api);
app.use(express.json());

app.listen(port, () => {
    console.log(`Server dang chay cong ${port}`)
})

const uri = 'mongodb+srv://hoalacanh2508:FnXN4Z9PhHQdRbcv@cluster0.x6cjq.mongodb.net/DATN_ToyStoryShop';

app.use((req, res, next) => {
    console.log(`[${new Date().toISOString()}] ${req.method} ${req.url}`);
    next();
});

mongoose.connect(uri, {
    connectTimeoutMS: 30000 // Thời gian chờ kết nối là 30 giây
})
.then(() => {
    console.log('Kết nối MongoDB thành công');
    

// Theo dõi thay đổi trong bộ sưu tập MongoDB
const orderCollection = mongoose.connection.collection('orders');
const chatCollection = mongoose.connection.collection('chats');
const orderChangeStream = orderCollection.watch();
const chatChangeStream = chatCollection.watch();

// Lắng nghe sự kiện thay đổi dữ liệu trong MongoDB
orderChangeStream.on('change', async (change) => {
    if (change.operationType === 'update') {
        const orderId = change.documentKey._id; // Lấy ID đơn hàng

        try {
            // Truy vấn tất cả thông tin của đơn hàng từ MongoDB
            const fullOrder = await OrderModel.findById(orderId).populate({
                path: 'prodDetails.prodId', // Liên kết tới sản phẩm
                select: 'namePro price'    // Chỉ lấy trường namePro và price
            });

            if (fullOrder) {
                console.log(`Order ID: ${orderId} có thay đổi, thông tin đầy đủ:`, fullOrder);

                // Gửi toàn bộ thông tin đơn hàng qua WebSocket
                wss.clients.forEach((client) => {
                    console.log(`Client trạng thái: ${client.readyState}`);
                    if (client.readyState === WebSocket.OPEN) {
                        console.log(`Gửi dữ liệu tới client: ${JSON.stringify(fullOrder)}`);
                        client.send(JSON.stringify(fullOrder));
                    }
                });
            } else {
                console.log(`Không tìm thấy đơn hàng với ID: ${orderId}`);
            }
        } catch (error) {
            console.error(`Lỗi khi lấy thông tin đơn hàng với ID: ${orderId}`, error);
        }
    }
});


chatChangeStream.on('change', async (change) => {

    if (change.operationType === 'insert') {
        // Lấy ID của tin nhắn mới
        const chatId = change.documentKey._id;

        try {
            // Truy vấn chi tiết tin nhắn mới
            const fullChat = await ChatModel.findById(chatId);

            if (fullChat) {
                console.log('Tin nhắn mới:', fullChat);

                // Gửi tin nhắn qua WebSocket tới tất cả client
                wss.clients.forEach((client) => {
                    if (client.readyState === WebSocket.OPEN) {
                        client.send(JSON.stringify(fullChat));
                    }
                });
            } else {
                console.log(`Không tìm thấy tin nhắn với ID: ${chatId}`);
            }
        } catch (error) {
            console.error(`Lỗi khi lấy thông tin tin nhắn với ID: ${chatId}`, error);
        }
    }
});

})
.catch(err => {
    console.error('Lỗi kết nối MongoDB:', err);
});


// Xử lý sự kiện kết nối WebSocket
const wss = new WebSocket.Server({ port: WS_PORT });
// Khởi chạy WebSocket server
console.log(`WebSocket server đang chạy trên cổng ${WS_PORT}`);
wss.on('connection', (ws) => {
    console.log('Client đã kết nối WebSocket');
    ws.send('Kết nối thành công với WebSocket server');
});

const productModel = require('./model/productModel');



app.get('/', async (req, res) => {
    try {
        let products = await productModel.find();
        console.log(products);
        res.json(products);
    } catch (error) {
        console.error('Lỗi khi truy vấn sản phẩm:', error);
        res.status(500).send('Lỗi server');
    }
});


exports.uri = uri;
exports.mongoose = mongoose;
exports.productModel = productModel;