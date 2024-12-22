import { Form, Input, Button, message, Select, Row, Col, Card, Switch, Upload } from "antd";
import { useState, useEffect } from "react";
import { addProduct } from "../../service/products"; // Service để thêm sản phẩm
import { getAllCategories } from "../../service/category"; // Service lấy danh mục sản phẩm
import { Icategory } from "../../interface/category"; // Interface cho danh mục
import { upload } from "../../service/upload"; // Service upload hình ảnh
import { useNavigate } from "react-router-dom"; // Thêm import useNavigate

const AddProduct = () => {
  const [form] = Form.useForm();
  const [messageApi, contextHolder] = message.useMessage();
  const [categories, setCategories] = useState<Icategory[]>([]); // Danh sách các danh mục
  const [imageFiles, setImageFiles] = useState<any[]>([]); // Lưu các tệp hình ảnh
  const [owerId, setOwerId] = useState<string>(""); // Sử dụng trường nhập liệu cho owerId
  const navigate = useNavigate(); // Khởi tạo navigate

  const info = () => {
    messageApi.open({
      type: "success",
      content: "Sản phẩm đã được thêm thành công!",
    });
  };

  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const data = await getAllCategories();
        setCategories(data);
      } catch (error) {
        console.log("Lỗi khi lấy danh mục:", error);
      }
    };
    fetchCategories();
  }, []);

  const uploadImages = async (files: any) => {
    const formData = new FormData();
    files.forEach((file: any) => {
      formData.append("images", file);
    });
    try {
      const res = await upload(formData);
      const imageUrls = res.payload.map((item: any) => item.url);
      const newFileList = imageUrls.map((url: string, index: number) => ({
        uid: `-${index}`,
        name: `image-${index}`,
        status: "done",
        url: url,
      }));
      setImageFiles([...imageFiles, ...newFileList]);
      return imageUrls;
    } catch (error) {
      console.log("Lỗi khi upload hình ảnh:", error);
      return [];
    }
  };

  const onFinish = async (values: any) => {
    const { namePro, price, quantity, desPro, cateId, brand, statusPro, listPro, creatDatePro, import_price } = values;

    const imageUrls = await uploadImages(imageFiles);

    const productData = {
      ...values,
      imgPro: imageUrls,
      creatDatePro: creatDatePro || new Date(),
      owerId: owerId,
    };

    try {
      const newProduct = await addProduct(productData);
      info();
      form.resetFields();

      // Chuyển hướng đến trang Dashboard sau khi thêm sản phẩm thành công
      navigate("/admin/dashboard"); // Đảm bảo đường dẫn "/dashboard" là đường dẫn hợp lệ trong ứng dụng của bạn
    } catch (error) {
      messageApi.open({
        type: "error",
        content: "Thêm sản phẩm thất bại!",
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
      <Card title="Thêm Sản Phẩm" bordered={true} style={{ width: "80%", margin: "0 auto", padding: "20px" }}>
        <Form form={form} onFinish={onFinish} layout="vertical">
          <Row gutter={24}>
            <Col xs={24} sm={12}>
              <Form.Item
                name="namePro"
                label="Tên sản phẩm"
                rules={[{ required: true, message: "Vui lòng nhập tên sản phẩm!" }]}>
                <Input placeholder="Nhập tên sản phẩm" />
              </Form.Item>
            </Col>
            <Col xs={24} sm={12}>
              <Form.Item
                name="owerId"
                label="Chủ sở hữu"
                rules={[{ required: true, message: "Vui lòng nhập owerId!" }]}>
                <Input
                  value={owerId}
                  onChange={(e) => setOwerId(e.target.value)}
                  placeholder="Nhập owerId"
                />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={24}>
            <Col xs={24} sm={12}>
              <Form.Item
                name="price"
                label="Giá sản phẩm"
                rules={[{ required: true, message: "Vui lòng nhập giá sản phẩm!" }]}>
                <Input type="number" placeholder="Nhập giá sản phẩm" />
              </Form.Item>
            </Col>
            <Col xs={24} sm={12}>
              <Form.Item
                name="quantity"
                label="Số lượng"
                rules={[{ required: true, message: "Vui lòng nhập số lượng sản phẩm!" }]}>
                <Input type="number" placeholder="Nhập số lượng" />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={24}>
            <Col xs={24} sm={12}>
              <Form.Item
                name="import_price"
                label="Giá Nhập"
                rules={[{ required: true, message: "Vui lòng nhập giá nhập sản phẩm!" }]}>
                <Input type="number" placeholder="Vui lòng nhập giá nhập" />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={24}>
            <Col xs={24} sm={12}>
              <Form.Item
                name="desPro"
                label="Mô tả sản phẩm">
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
              <Form.Item
                name="cateId"
                label="Danh mục"
                rules={[{ required: true, message: "Vui lòng chọn danh mục sản phẩm!" }]}>
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
              <Form.Item
                name="listPro"
                label="Mô hình sản phẩm">
                <Input placeholder="Nhập mô hình sản phẩm" />
              </Form.Item>
            </Col>
            <Col xs={24} sm={12}>
              <Form.Item
                name="creatDatePro"
                label="Ngày tạo sản phẩm"
                rules={[{ required: true, message: "Vui lòng nhập ngày tạo!" }]}>
                <Input type="date" />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={24}>
            <Col xs={24} sm={12}>
              <Form.Item
                name="imgPro"
                label="Hình ảnh sản phẩm">
                <Upload
                  beforeUpload={(file) => {
                    setImageFiles((prev) => [...prev, file]);
                    return false;
                  }}
                  listType="picture-card"
                  fileList={imageFiles}
                  accept="image/*">
                  <Button>Chọn hình ảnh</Button>
                </Upload>
              </Form.Item>
            </Col>
          </Row>

          <Form.Item>
            <Button type="primary" htmlType="submit" style={{ width: "100%" }}>
              Thêm sản phẩm
            </Button>
          </Form.Item>
        </Form>
      </Card>
    </>
  );
};

export default AddProduct;
