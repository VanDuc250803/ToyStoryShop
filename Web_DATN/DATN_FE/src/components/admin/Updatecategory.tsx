import React, { useEffect, useState } from 'react';
import { Form, Input, message, Button, Card, Popconfirm } from 'antd';
import { useNavigate, useParams } from 'react-router-dom';
import { Icategory } from '../../interface/category';
import { getCategoryByID, updateCategory } from '../../service/category';

type Props = {};

const Updatecategory = (props: Props) => {
  const [category, setCategory] = useState<Icategory[]>([]);
  const [messageApi, contextHolder] = message.useMessage();
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const { id } = useParams();

  useEffect(() => {
    const fetchCategory = async () => {
      try {
        const response = await getCategoryByID(id);
        form.setFieldsValue({
          name: response.name,
        });
        console.log(response);
      } catch (error) {
        console.error(error);
      }
    };
    fetchCategory();
  }, [id, form]);

  const info = () => {
    messageApi.open({
      type: 'success',
      content: 'Category updated successfully',
    });
  };

  const onFinish = async (values: any) => {
    try {
      const categoryData = { ...values };
      const updatedCategory = await updateCategory(id, categoryData);

      if (updatedCategory) {
        console.log('Updated Category:', updatedCategory);
        info();
        form.resetFields();
        navigate('/admin/category');
      } else {
        messageApi.error('Failed to update category');
      }
    } catch (error) {
      console.error('Error updating category:', error);
      messageApi.error('Server Error: Could not update category.');
    }
  };

  const handleCancel = () => {
    navigate(-1); // Quay lại trang trước
  };

  return (
    <>
      <div className="pt-[20px] px-[30px]">
        <div className="space-y-6 font-[sans-serif] max-w-md mx-auto">
          <Card
            title="Cập Nhật Danh Mục"
            bordered={false}
            style={{
              width: '100%',
              boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)',
              borderRadius: '8px',
            }}
          >
            <Form form={form} onFinish={onFinish}>
              <div className="mb-6">
                <label className="mb-2 text-xl text-gray-700 block">Tên Danh Mục:</label>
                <Form.Item
                  name="name"
                  rules={[{ required: true, message: 'Vui lòng nhập tên danh mục!' }]}
                >
                  <Input
                    className="pr-4 pl-5 py-3 text-sm text-black rounded-lg border border-gray-300 w-full focus:outline-none focus:ring-2 focus:ring-blue-500"
                    placeholder="Nhập tên danh mục"
                  />
                </Form.Item>
              </div>

              <Form.Item>
                <Button
                  type="primary"
                  htmlType="submit"
                  block
                  size="large"
                  className="bg-blue-600 hover:bg-blue-700 focus:ring-2 focus:ring-blue-500 transition duration-300 ease-in-out"
                >
                  Cập Nhật Danh Mục
                </Button>
              </Form.Item>

              {/* Nút Hủy với xác nhận */}
              <Form.Item>
                <Popconfirm
                  title="Bạn có chắc chắn muốn hủy không?"
                  description="Các thay đổi trước đó sẽ không được lưu."
                  onConfirm={handleCancel}
                  okText="Có"
                  cancelText="Không"
                >
                  <Button
                    type="default"
                    block
                    size="large"
                    className="mt-4 bg-gray-300 hover:bg-gray-400 transition duration-300 ease-in-out"
                  >
                    Hủy
                  </Button>
                </Popconfirm>
              </Form.Item>
            </Form>
          </Card>
        </div>
      </div>
    </>
  );
};

export default Updatecategory;
