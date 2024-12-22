import { Form, Input, Button, message, Select, Row, Col, Card, Switch, Upload } from "antd";
import { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom"; 
import { getProductByID, updateProduct } from "../../service/products"; 
import { getAllCategories } from "../../service/category"; 
import { Icategory } from "../../interface/category"; 

const UpdateProduct = () => {
  const { id } = useParams(); 
  const [form] = Form.useForm();
  const [messageApi, contextHolder] = message.useMessage();
  const [categories, setCategories] = useState<Icategory[]>([]); 
  const [imageFiles, setImageFiles] = useState<any[]>([]); 
  const [product, setProduct] = useState<any>(null); 
  const navigate = useNavigate(); 

  useEffect(() => {
    const fetchProductData = async () => {
      try {
        const data = await getProductByID(id); 
        setProduct(data);
        form.setFieldsValue({
          ...data,
          creatDatePro: data.creatDatePro ? data.creatDatePro.substring(0, 10) : "",
        });
        setImageFiles(data.imgPro?.map((url: string) => ({ url })) || []);
      } catch (error) {
        console.log("Lỗi khi lấy sản phẩm:", error);
      }
    };

    const fetchCategories = async () => {
      try {
        const data = await getAllCategories();
        setCategories(data);
      } catch (error) {
        console.log("Lỗi khi lấy danh mục:", error);
      }
    };

    if (id) {
      fetchProductData();
    }
    fetchCategories();
  }, [id]);

  const handleUpdate = async (values: any) => {
    const { namePro, price, quantity, desPro, cateId, brand, statusPro, listPro, creatDatePro, import_price } = values;

    // Kiểm tra ảnh: Dùng ảnh mới nếu có, hoặc giữ ảnh cũ nếu không chỉnh sửa
    const imageUrls =
      imageFiles.length > 0
        ? imageFiles.map((file: any) => file.url || file.thumbUrl) // URL ảnh mới
        : product?.imgPro; // Ảnh cũ

    const productData = {
      ...values,
      imgPro: imageUrls,
      creatDatePro: creatDatePro || new Date(),
    };

    try {
      await updateProduct(id, productData); 
      messageApi.open({
        type: "success",
        content: "Sản phẩm đã được cập nhật thành công!",
      });
      navigate("/admin/dashboard");
    } catch (error) {
      messageApi.open({
        type: "error",
        content: "Cập nhật sản phẩm thất bại!",
      });
    }
  };

  const handleChange = (info: any) => {
    if (info.fileList) {
      setImageFiles(info.fileList);
    }
  };

  return (
    <>
      {contextHolder}
      <Card title="Cập nhật Sản Phẩm" bordered={true} style={{ width: "80%", margin: "0 auto", padding: "20px" }}>
        <Form form={form} onFinish={handleUpdate} layout="vertical">
          <Row gutter={24}>
            <Col xs={24} sm={12}>
              <Form.Item name="namePro" label="Tên sản phẩm" rules={[{ required: true, message: "Vui lòng nhập tên sản phẩm!" }]}>
                <Input placeholder="Nhập tên sản phẩm" />
              </Form.Item>
            </Col>
            <Col xs={24} sm={12}>
              <Form.Item name="owerId" label="Chủ sở hữu" rules={[{ required: true, message: "Vui lòng nhập owerId!" }]}>
                <Input placeholder="Nhập owerId" />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={24}>
            <Col xs={24} sm={12}>
              <Form.Item name="price" label="Giá sản phẩm" rules={[{ required: true, message: "Vui lòng nhập giá sản phẩm!" }]}>
                <Input type="number" placeholder="Nhập giá sản phẩm" />
              </Form.Item>
            </Col>
            <Col xs={24} sm={12}>
              <Form.Item name="quantity" label="Số lượng" rules={[{ required: true, message: "Vui lòng nhập số lượng sản phẩm!" }]}>
                <Input type="number" placeholder="Nhập số lượng" />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={24}>
            <Col xs={24} sm={12}>
              <Form.Item name="import_price" label="Giá Nhập" rules={[{ required: true, message: "Vui lòng nhập giá nhập sản phẩm!" }]}>
                <Input type="number" placeholder="Vui lòng nhập giá nhập" />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={24}>
            <Col xs={24} sm={12}>
              <Form.Item name="desPro" label="Mô tả sản phẩm">
                <Input.TextArea placeholder="Nhập mô tả sản phẩm" maxLength={255} />
              </Form.Item>
            </Col>
            <Col xs={24} sm={12}>
              <Form.Item name="statusPro" label="Trạng thái" valuePropName="checked">
                <Switch checkedChildren="Còn hàng" unCheckedChildren="Hết hàng" />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={24}>
            <Col xs={24} sm={12}>
              <Form.Item name="cateId" label="Danh mục" rules={[{ required: true, message: "Vui lòng chọn danh mục sản phẩm!" }]}>
                <Select placeholder="Chọn danh mục sản phẩm">
                  {categories.map((category) => (
                    <Select.Option key={category._id} value={category._id}>
                      {category.name}
                    </Select.Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
            <Col xs={24} sm={12}>
              <Form.Item name="brand" label="Thương hiệu">
                <Input placeholder="Nhập thương hiệu sản phẩm" />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={24}>
            <Col xs={24} sm={12}>
              <Form.Item name="listPro" label="Mô hình sản phẩm">
                <Input placeholder="Nhập mô hình sản phẩm" />
              </Form.Item>
            </Col>
            <Col xs={24} sm={12}>
              <Form.Item name="creatDatePro" label="Ngày tạo sản phẩm">
                <Input type="date" />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={24}>
            <Col xs={24} sm={12}>
              <Form.Item name="imgPro" label="Hình ảnh sản phẩm">
                <Upload
                  beforeUpload={(file) => {
                    setImageFiles((prev) => [...prev, file]);
                    return false;
                  }}
                  listType="picture-card"
                  fileList={imageFiles}
                  accept="image/*"
                  onChange={handleChange}
                >
                  <Button>Chọn hình ảnh</Button>
                </Upload>
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={24}>
            <Col xs={12} sm={12}>
              <Form.Item>
                <Button type="default" onClick={() => navigate("/admin/dashboard")} style={{ width: "100%" }}>
                  Quay lại
                </Button>
              </Form.Item>
            </Col>
            <Col xs={12} sm={12}>
              <Form.Item>
                <Button type="primary" htmlType="submit" style={{ width: "100%" }}>
                  Cập nhật sản phẩm
                </Button>
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </Card>
    </>
  );
};

export default UpdateProduct;
