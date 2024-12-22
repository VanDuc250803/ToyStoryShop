import React, { useEffect, useState } from "react";
import axios from "axios";
import "./DonHang.css";

// Định nghĩa kiểu dữ liệu cho đơn hàng
interface Order {
  _id: string;
  cusId: string;
  revenue_all: number;
  name_order: string;
  phone_order: string;
  address_order: string;
  payment_method: string;
  prodDetails: {
    prodId: {
      _id: string;
      namePro: string;
    };
    revenue: number;
    quantity: number;
    prodSpecification: string;
  }[];
  content: string;
  orderStatus: string;
  orderDate: string;
  __v: number;
}


const DonHang: React.FC = () => {
  const [orders, setOrders] = useState<Order[]>([]);
  const [selectedOrder, setSelectedOrder] = useState<Order | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [currentTab, setCurrentTab] = useState<string>("Tất cả");
  const [currentSubTab, setCurrentSubTab] = useState<string>("Đã xong");
  const [searchTerm, setSearchTerm] = useState<string>("");

  useEffect(() => {
    axios
      .get("http://localhost:28017/orders")
      .then((response) => {
        console.log(response.data);
        setOrders(response.data);
      })
      .catch((error) => {
        console.error("Lỗi khi lấy danh sách đơn hàng:", error);
      });
  }, []);

  const handleStatusChange = (id: string, newStatus: string) => {
    const order = orders.find((order) => order._id === id);
    const customerName = order?.name_order || "Khách hàng";

    // Cập nhật trạng thái đơn hàng
    setOrders((prevOrders) =>
      prevOrders.map((order) =>
        order._id === id ? { ...order, orderStatus: newStatus } : order
      )
    );
    if (newStatus === "Đã giao") {
      setCurrentTab("Đã giao");
      setCurrentSubTab("Hoàn hàng");
    }
    axios
      .put(`http://localhost:28017/orders/${id}`, { orderStatus: newStatus })
      .then(() => {
        alert(`Đơn hàng của khách hàng ${customerName} đã được chuyển sang trạng thái ${newStatus}`);
      })
      .catch((error) => {
        console.error("Lỗi khi cập nhật trạng thái:", error);
        alert("Đã xảy ra lỗi khi cập nhật trạng thái đơn hàng.");
      });
  };

  const tabs = ["Tất cả", "Chờ xác nhận", "Chờ lấy hàng", "Chờ giao hàng", "Đã giao", "Đã hủy"];
  const subTabs = ["Đã xong", "Hoàn hàng"]; // Đổi "Hoàn trả" thành "Hoàn hàng"
  const filteredOrders =
  currentTab === "Đã giao"
    ? currentSubTab === "Hoàn hàng"
      ? orders.filter((order) => order.orderStatus === "Hoàn hàng")
      : orders.filter((order) => order.orderStatus === "Đã giao")
    : currentTab === "Tất cả"
    ? orders.filter((order) => order.orderStatus !== "Đã hủy")
    : orders.filter((order) => order.orderStatus === currentTab);





  const searchedOrders = filteredOrders.filter((order) =>
    order.phone_order.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const handleNameClick = (order: Order) => {
    setSelectedOrder(order);
    setIsModalOpen(true);
  };

  const closeModal = () => {
    setSelectedOrder(null);
    setIsModalOpen(false);
  };

  return (
    <div className="don-hang-container">
      <h1 className="title">Danh sách đơn hàng</h1>
      {/* Tab Buttons */}
      <div className="tabs">
        {tabs.map((tab) => (
          <button
            key={tab}
            className={`tab-button ${currentTab === tab ? "active" : ""}`}
            onClick={() => setCurrentTab(tab)}
          >
            {tab}
          </button>
        ))}
      </div>
      {currentTab === "Đã giao" && (
        <div className="sub-tabs">
          <button
            className={`sub-tab-button ${currentSubTab === "Đã xong" ? "active" : ""}`}
            onClick={() => setCurrentSubTab("Đã xong")}
          >
            Đã xong
          </button>
          <button
            className={`sub-tab-button ${currentSubTab === "Hoàn hàng" ? "active" : ""}`}
            onClick={() => setCurrentSubTab("Hoàn hàng")}
          >
            Hoàn hàng
          </button>
        </div>
      )}

      <div className="search-container">
        <input
          type="text"
          placeholder="Tìm theo số điện thoại..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="search-input"
        />
      </div>
      {filteredOrders.length === 0 ? (
        <p className="no-orders">Không có đơn hàng nào.</p>
      ) : (
        <table className="orders-table">
          <thead>
            <tr>
              <th>Tên khách hàng</th>
              <th>Điện thoại</th>
              <th>Ngày tạo đơn</th>
              <th>Địa chỉ</th>
              <th>Tổng tiền</th>
              <th>Phương thức thanh toán</th>
              <th>Trạng thái</th>
              <th>Xem chi tiết</th>
            </tr>
          </thead>
          <tbody>
            {searchedOrders.map((order) => (
              <tr key={order._id}>
                <td>{order.name_order}</td>
                <td>{order.phone_order}</td>
                <td>
                  {new Date(order.orderDate).toLocaleDateString("vi-VN", {
                    year: "numeric",
                    month: "2-digit",
                    day: "2-digit",
                  })}
                </td>
                <td>{order.address_order}</td>
                <td>{order.revenue_all.toLocaleString()} VND</td>
                <td>{order.payment_method}</td>
                <td>
                  {currentTab === "Đã hủy" ? (
                    <span style={{ color: "red", fontWeight: "bold" }}>Đã hủy</span>
                  ) : (
                    <select
                      value={order.orderStatus}
                      onChange={(e) => handleStatusChange(order._id, e.target.value)}
                    >
                      <option
                        value="Chờ xác nhận"
                        disabled={order.orderStatus !== "Chờ xác nhận"}
                        style={{ color: order.orderStatus === "Chờ xác nhận" ? "red" : "gray" }}
                      >
                        Chờ xác nhận
                      </option>
                      <option
                        value="Chờ lấy hàng"
                        disabled={order.orderStatus !== "Chờ xác nhận" && order.orderStatus !== "Chờ lấy hàng"}
                        style={{ color: order.orderStatus === "Chờ lấy hàng" ? "orange" : "gray" }}
                      >
                        Chờ lấy hàng
                      </option>
                      <option
                        value="Chờ giao hàng"
                        disabled={order.orderStatus !== "Chờ lấy hàng" && order.orderStatus !== "Chờ giao hàng"}
                        style={{ color: order.orderStatus === "Chờ giao hàng" ? "blue" : "gray" }}
                      >
                        Chờ giao hàng
                      </option>
                      <option
                        value="Đã giao"
                        disabled={order.orderStatus !== "Chờ giao hàng"}
                        style={{ color: order.orderStatus === "Đã giao" ? "green" : "gray" }}
                      >
                        Đã giao
                      </option>
                      <option
                        value="Hoàn hàng"
                        disabled={true} // Luôn vô hiệu hóa
                        style={{ color: order.orderStatus === "Hoàn hàng" ? "purple" : "gray"}}
                      >
                        Hoàn hàng
                      </option>
                    </select>
                  )}
                </td>



                <td>
                  <button onClick={() => handleNameClick(order)} className="details-button">
                    Chi tiết
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      {isModalOpen && selectedOrder && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h2>Thông tin đơn hàng</h2>
            <div className="order-summary">
              <p><strong>Tên khách hàng:</strong> {selectedOrder.name_order}</p>
              <p><strong>Số điện thoại:</strong> {selectedOrder.phone_order}</p>
              <p><strong>Địa chỉ:</strong> {selectedOrder.address_order}</p>
              <p><strong>Ngày đặt hàng:</strong>
                {new Date(selectedOrder.orderDate).toLocaleDateString("vi-VN", {
                  year: "numeric",
                  month: "2-digit",
                  day: "2-digit",
                })}
              </p>
              <p><strong>Phương thức thanh toán:</strong> {selectedOrder.payment_method}</p>
              <p><strong>Tổng tiền:</strong> {selectedOrder.revenue_all.toLocaleString()} VND</p>
              <p><strong>Nội dung:</strong> {selectedOrder.content}</p>

            </div>
            <h3>Danh sách sản phẩm</h3>
            <div className="product-list">
              {selectedOrder.prodDetails.map((prod, index) => (
                <div key={index} className="product-item">
                  <p><strong>Tên sản phẩm:</strong> {prod.prodId?.namePro || "Không xác định"}</p>
                  <p><strong>Số lượng:</strong> {prod.quantity}</p>
                  <p><strong>Phân loại:</strong> {prod.prodSpecification || "Không có phân loại"}</p>
                  <p><strong>Doanh thu:</strong> {prod.revenue.toLocaleString()} VND</p>
                </div>
              ))}
            </div>
            <button onClick={closeModal}>Đóng</button>
          </div>
        </div>
      )}
    </div>
  );
};

export default DonHang;
