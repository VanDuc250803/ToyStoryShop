import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { getProductByID } from '../../service/products';
import Slider from 'react-slick';

const ProductDetails = () => {
  const { id } = useParams();
  const [product, setProduct] = useState<any>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>('');

  useEffect(() => {
    const fetchProductData = async () => {
      try {
        const data = await getProductByID(id); // Gọi API để lấy dữ liệu sản phẩm
        if (data) {
          setProduct(data); // Lưu dữ liệu sản phẩm vào state
        } else {
          setError('Không tìm thấy sản phẩm');
        }
      } catch (err) {
        console.error('Failed to fetch product by ID:', err);
        setError('Có lỗi xảy ra khi tải sản phẩm');
      } finally {
        setLoading(false); // Đảm bảo loading luôn được cập nhật
      }
    };

    fetchProductData();
  }, [id]);

  if (loading) return <div>Đang tải...</div>;
  if (error) return <div>Lỗi: {error}</div>;

  const sliderSettings = {
    autoplay: true, // Tự động chuyển ảnh
    autoplaySpeed: 3000, // Thời gian chuyển ảnh (3 giây)
    dots: false, // Ẩn dấu chấm
    infinite: true, // Cho phép chuyển tiếp vòng quay
    speed: 500,
    slidesToShow: 1,
    slidesToScroll: 1,
  };

  return (
    <div className="flex flex-col lg:flex-row gap-6 bg-gray-100 p-6 rounded-lg shadow-lg max-w-4xl mx-auto">
      {/* Slideshow ảnh */}
      {product?.imgPro && product.imgPro.length > 0 && (
        <div className="lg:w-1/3">
          <Slider {...sliderSettings}>
            {product.imgPro.map((img: string, index: number) => (
              <div key={index}>
                <img
                  src={img}
                  alt={`Product Image ${index + 1}`}
                  className="w-full h-auto rounded-lg shadow-md"
                />
              </div>
            ))}
          </Slider>
        </div>
      )}

      {/* Nội dung chi tiết */}
      <div className="flex-1">
        <h1 className="text-3xl font-bold text-gray-900 mb-4">{product.namePro}</h1>
        <p className="text-gray-700 text-base mb-4">{product.desPro}</p>
        <div className="space-y-2">
          <p className="text-lg font-medium text-gray-800">Giá: {product.price}VND</p>
          <p className="text-lg font-medium text-red-600 font-bold">Giá nhập: {product.import_price}VND</p>
          <p className="text-lg font-medium text-gray-800">Số lượng: {product.quantity}</p>
          <p
            className={`text-lg font-medium ${product.statusPro ? 'text-green-600' : 'text-red-600'}`}
          >
            Trạng thái: {product.statusPro ? 'Còn hàng' : 'Hết hàng'}
          </p>
          <p className="text-gray-700 text-base">Thương hiệu: {product.brand}</p>
          <p className="text-gray-700 text-base">
            Ngày tạo: {new Date(product.creatDatePro).toLocaleDateString()}
          </p>
        </div>
        <button
          onClick={() => window.history.back()}
          className="mt-6 px-6 py-2 bg-blue-500 text-white font-medium text-lg rounded hover:bg-blue-600 transition duration-300"
        >
          Quay lại
        </button>
      </div>
    </div>
  );
};

export default ProductDetails;
