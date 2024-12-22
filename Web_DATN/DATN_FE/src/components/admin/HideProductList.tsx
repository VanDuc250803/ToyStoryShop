import React, { useEffect, useState } from "react";
import { Container, Typography, Grid, Card, CardContent, CardMedia, Button, Box, IconButton } from "@mui/material";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import { useNavigate } from "react-router-dom";
import axios from "axios";

interface HideProduct {
  _id: string;
  namePro: string;
  statusPro: boolean;
  price: number;
  quantity: number;
  cateId: string;
  imgPro: string[];
}

const HideProductList: React.FC = () => {
  const [hiddenProducts, setHiddenProducts] = useState<HideProduct[]>([]);
  const [error, setError] = useState<string | null>(null); // Thêm state để lưu lỗi
  const navigate = useNavigate(); // Sử dụng hook để điều hướng

  useEffect(() => {
    // Lấy danh sách sản phẩm ẩn từ API
    axios
      .get("http://localhost:28017/hideproducts") // Địa chỉ API của bạn
      .then((response) => {
        setHiddenProducts(response.data);
      })
      .catch((error) => {
        console.error("Có lỗi khi lấy danh sách sản phẩm ẩn:", error);
        setError("Lỗi khi tải dữ liệu sản phẩm ẩn! Vui lòng kiểm tra kết nối hoặc API.");
      });
  }, []);

  const handleShowProduct = (id: string) => {
    axios
      .put(`http://localhost:28017/hideproducts/show/${id}`) // Nếu backend API là /product/show/:id
      .then((response) => {
        console.log("Sản phẩm đã được hiển thị:", response.data);  // In kết quả từ server
        setHiddenProducts((prevProducts) =>
          prevProducts.filter((product) => product._id !== id)
        );
        navigate("/admin/dashboard");
      })
      .catch((error) => {
        if (error.response) {
          console.error("Lỗi từ API:", error.response.data);
          setError(error.response.data.message || "Lỗi khi cập nhật sản phẩm!");
        } else {
          console.error("Lỗi kết nối hoặc server không phản hồi:", error);
          setError("Lỗi khi cập nhật sản phẩm! Vui lòng thử lại.");
        }
      });
  };
  
  return (
    <Container sx={{ marginTop: 4 }}>
      {/* Nút quay lại */}
      <Box sx={{ display: "flex", alignItems: "center", marginBottom: 2 }}>
        <IconButton onClick={() => navigate(-1)} color="primary">
          <ArrowBackIcon />
        </IconButton>
        <Typography variant="h5" sx={{ marginLeft: 1 }}>
          Danh sách sản phẩm ẩn
        </Typography>
      </Box>

      {/* Hiển thị thông báo lỗi nếu có */}
      {error && (
        <Typography variant="body1" color="error" textAlign="center">
          {error}
        </Typography>
      )}

      {/* Hiển thị thông báo nếu không có sản phẩm ẩn */}
      {hiddenProducts.length === 0 ? (
        <Typography variant="body1" color="text.secondary" textAlign="center">
          Không có sản phẩm ẩn.
        </Typography>
      ) : (
        <Grid container spacing={4}>
          {hiddenProducts.map((product) => (
            <Grid item xs={12} sm={6} md={4} key={product._id}>
              <Card sx={{ maxWidth: 345 }}>
                <CardMedia
                  component="img"
                  height="200"
                  image={product.imgPro[0]} // Hiển thị ảnh sản phẩm (dùng ảnh đầu tiên trong mảng imgPro)
                  alt={product.namePro}
                />
                <CardContent>
                  <Typography gutterBottom variant="h6" component="div">
                    {product.namePro}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Giá: {product.price.toLocaleString()} VNĐ
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Số lượng: {product.quantity}
                  </Typography>
                </CardContent>
                <Box sx={{ display: "flex", justifyContent: "center", paddingBottom: 2 }}>
                  {/* <Button variant="contained" color="primary" size="small">
                    Xem Chi Tiết
                  </Button> */}
                </Box>
                {/* Thêm nút Hiện lại sản phẩm */}
                <Box sx={{ display: "flex", justifyContent: "center", paddingBottom: 2 }}>
                  {/* <Button
                    variant="outlined"
                    color="secondary"
                    size="small"
                    onClick={() => handleShowProduct(product._id)}
                  >
                    Hiện lại sản phẩm
                  </Button> */}
                </Box>
              </Card>
            </Grid>
          ))}
        </Grid>
      )}
    </Container>
  );
};

export default HideProductList;
