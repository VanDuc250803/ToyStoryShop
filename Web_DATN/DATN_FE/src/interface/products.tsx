import mongoose, { Document } from "mongoose";
import { Icategory } from "./category"; // Đảm bảo rằng bạn đã khai báo Icategory đúng

// Interface mô tả sản phẩm đầy đủ
export interface Iproduct extends Document {
  _id: string;                             // ID mặc định của MongoDB
  owerId: mongoose.Schema.Types.ObjectId;  // ID chủ sở hữu (đã sửa lại từ owerId)
  statusPro?: boolean;                     // Trạng thái sản phẩm (Còn hàng hay hết hàng)
  price: number;                           // Giá bán sản phẩm
  desPro?: string;                         // Mô tả sản phẩm
  creatDatePro?: string;                   // Ngày tạo
  quantity: number;                        // Số lượng sản phẩm trong kho
  listPro?: string;                        // Danh mục phân loại (có thể là tên hoặc ID của danh mục)
  imgPro?: string[];                       // Hình ảnh sản phẩm (mảng URL)
  namePro: string;                         // Tên sản phẩm
  cateId: mongoose.Schema.Types.ObjectId;  // ID danh mục liên kết
  brand?: string;                          // Thương hiệu sản phẩm
  import_price: number;                    // Giá nhập sản phẩm (bắt buộc)
  isHidden?: boolean;                      // Trạng thái ẩn/hiện của sản phẩm
}

// Đối với kiểu IProductLite, chỉ lấy những trường cần thiết
export type IProductLite = Pick<Iproduct, "namePro" | "imgPro" | "price" | "cateId">;
