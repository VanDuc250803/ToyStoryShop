import React, { useState } from 'react'
import { Form, Input, message, Button, Card } from "antd";
import { useNavigate } from 'react-router-dom';
import { Icategory } from '../../interface/category';
import { addCategory } from '../../service/category';

type Props = {}

const Addcategory = (props: Props) => {
    const [category, setCategory] = useState<Icategory[]>([]);
    const [messageApi, contextHolder] = message.useMessage();
    const navigate = useNavigate();
    const [form] = Form.useForm();

    const info = () => {
        messageApi.open({
            type: "success",
            content: "Category added successfully",
        });
    };

    const onFinish = async (values: any) => {
        console.log("Success:", values);
        
        const payload = {
            ...values,
        };
        
        const category = await addCategory(payload);
        console.log(category);

        const newCategory = [category];
        setCategory(newCategory);
        
        info();
        navigate("/admin/category");
    };

    return (
        <>
            <div className="pt-[20px] px-[30px]">
                <div className="space-y-6 font-[sans-serif] max-w-md mx-auto">
                    <Card
                        title="Thêm Mới Danh Mục"
                        bordered={false}
                        style={{ width: '100%', boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)', borderRadius: '8px' }}
                    >
                        <Form form={form} onFinish={onFinish}>
                            <div className="mb-6">
                                <label className="mb-2 text-xl text-gray-700 block">Tên Danh Mục:</label>
                                <Form.Item
                                    name="name"
                                    rules={[{ required: true, message: "Vui lòng nhập tên danh mục!" }]}
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
                                    Thêm Mới Danh Mục
                                </Button>
                            </Form.Item>
                        </Form>
                    </Card>
                </div>
            </div>
        </>
    );
}

export default Addcategory;
