import React from "react";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import Admin from "./layout/Admin";
import Dashboard from "./components/admin/Dashboard";
import Add from "./components/admin/add";
import Update from "./components/admin/update";
import Login from "./components/login";
import Register from "./components/register";
import Privaterouter from "./components/privaterouter";
import Addcategory from "./components/admin/Category";
import Updatecategory from "./components/admin/Updatecategory";
import Listcategory from "./components/admin/Category";
import Addcate from "./components/admin/addCategory";
import BarChartComponent from "./components/chart/BarChartComponent";
import NhanVien from "./components/nhanVien/nhan_vien";
import ProductDetails from "./components/admin/ProductDetail";
import DonHang from "./components/donHang/donhang";
import VoucherManager from "./components/voucher/Voucher";
import EditVoucher from "./components/voucher/editVoucher";
import AddVoucher from "./components/voucher/addVoucher";
import FeedbackMenu from "./components/feedback/FeedBack";
import FeedbackKH from "./components/feedback/FeedbackKH";
import FeedbackApp from "./components/feedback/FeedBackApp";
import Chat from "./components/trochuyen/Chat";
import NewArtStory from "./components/tintuc/News";
import EditArtStory from "./components/tintuc/EditArtStory";
import AddArtStory from "./components/tintuc/AddArtStory";
import ArtStoryDetail from "./components/tintuc/ArtStoryDetail";
import Transaction from "./components/admin/Transaction";
import HoanHang from "./components/refuns/HoanHang";
import HideProductList from "./components/admin/HideProductList";



function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Login và Register */}
        <Route path="/login" element={<Login />} />
        <Route path="/" element={<Login />} />
        <Route path="/register" element={<Register />} />

        {/* Admin Layout */}
        <Route path="/admin" element={<Privaterouter><Admin /></Privaterouter>}>
          {/* Dashboard */}
          <Route path="dashboard" element={<Dashboard />} />
          <Route path="transaction" element={<Transaction/>} />
          {/* Chi tiết sản phẩm */}
          <Route path="dashboard/product/details/:id" element={<ProductDetails />} />
          <Route path="dashboard/hidden-products" element={<HideProductList />} />

          {/* Quản lý sản phẩm */}
          <Route path="add" element={<Add />} />
          <Route path="dashboard/:id" element={<Update />} />

          {/* Quản lý danh mục */}
          <Route path="category" element={<Listcategory />} />
          <Route path="addcategory" element={<Addcate />} />
          <Route path="category/updatecategory/:id" element={<Updatecategory />} />

          {/* Quản lý nhân viên */}
          <Route path="staff" element={<NhanVien />} />
          {/* Quản lý đơn hàng */}
          <Route path="donhang" element={<DonHang />} />
          <Route path="hoanHang" element={<HoanHang />} />
          {/* Quản lý chăm sóc khách hàng */}
          <Route path="tro_chuyen" element={<Chat />} />

          {/* Quản lý voucher */}
          <Route path="voucher" element={<VoucherManager />} />
          <Route path="addVoucher" element={<AddVoucher />} />
          <Route path="editvoucher/:id" element={<EditVoucher />} />

          {/* Quản lý feedback */}
          <Route path="feedback" element={<FeedbackMenu />} />
          <Route path="feedbackApp" element={<FeedbackApp />} />
          <Route path="feedbackKH" element={<FeedbackKH />} />

          {/* Quản lý tin tức */}
          <Route path="news" element={<NewArtStory />} />
          <Route path="AddArtStory" element={<AddArtStory />} />
          <Route path="EditArtStory/:id" element={<EditArtStory />} />
          <Route path="ArtStoryDetail/:id" element={<ArtStoryDetail />} />


          {/* Thống kê */}
          <Route path="thongke" element={<BarChartComponent />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;
