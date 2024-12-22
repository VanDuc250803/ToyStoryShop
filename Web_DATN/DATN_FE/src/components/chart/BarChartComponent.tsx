import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';

interface TopProduct {
  prodId: string;
  name: string;
  image: string[];
  revenue: number;
  profit: number;
  quantity: number;
}

interface AnalyticsData {
  totalRevenue: number;
  totalProfit: number;
  topProduct: TopProduct;
}

const AnalyticsDashboard: React.FC = () => {
  const [analytics, setAnalytics] = useState<AnalyticsData | null>(null);
  const [startDate, setStartDate] = useState<string>('2024-12-09');
  const [endDate, setEndDate] = useState<string>('2024-12-31');
  const [loading, setLoading] = useState<boolean>(false);

  const fetchAnalytics = async () => {
    setLoading(true);
    try {
      const response = await axios.get('http://localhost:28017/analytics', {
        params: { startDate, endDate },
      });
      setAnalytics(response.data);
    } catch (error) {
      console.error('Lỗi khi lấy dữ liệu phân tích:', error);
      setAnalytics(null);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAnalytics();
  }, [startDate, endDate]);

  // Dữ liệu cho biểu đồ
  const chartData = [
    {
      name: 'Tổng doanh thu',
      value: analytics ? analytics.totalRevenue : 0,
    },
    {
      name: 'Tổng lợi nhuận',
      value: analytics ? analytics.totalProfit : 0,
    },
  ];

  return (
    <div style={styles.container}>
      <h2 style={styles.title}>Thống kê Doanh thu</h2>

      <div style={styles.dateContainer}>
        <label style={styles.label}>
          Ngày bắt đầu:
          <input
            type="date"

            value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
            style={styles.dateInput}
          />
        </label>
        <label style={styles.label}>
          Ngày kết thúc:
          <input
            type="date"
            value={endDate}
            onChange={(e) => setEndDate(e.target.value)}
            style={styles.dateInput}
          />
        </label>
      </div>

      {loading ? (
        <p style={styles.loadingText}>Đang tải dữ liệu...</p>
      ) : analytics ? (
        <div style={styles.analyticsContainer}>
          <div style={styles.summaryContainer}>
            <p>Tổng doanh thu: <strong>{analytics.totalRevenue?.toLocaleString()} VNĐ</strong></p>
            <p>Tổng lợi nhuận: <strong>{analytics.totalProfit?.toLocaleString()} VNĐ</strong></p>
          </div>

          <div style={styles.chartContainer}>
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={chartData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="name" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Bar dataKey="value" fill="#8884d8" />
              </BarChart>
            </ResponsiveContainer>
          </div>

          <div style={styles.productContainer}>
            <h3>Sản phẩm bán chạy nhất</h3>
            <p><strong>{analytics.topProduct.name}</strong></p>
            <div style={styles.imageGallery}>
              {analytics.topProduct.image.slice(0, 4).map((img, index) => (
                <img key={index} src={img} alt={`Image ${index + 1}`} style={styles.productImage} />
              ))}
            </div>
            <p>Doanh thu: <strong>{analytics.topProduct.revenue?.toLocaleString()} VNĐ</strong></p>
            <p>Lợi nhuận: <strong>{analytics.topProduct.profit?.toLocaleString()} VNĐ</strong></p>
            <p>Số lượng: <strong>{analytics.topProduct.quantity}</strong></p>
          </div>
        </div>
      ) : (
        <p style={styles.noDataText}>Không có dữ liệu để hiển thị.</p>
      )}
    </div>
  );
};

const styles = {
  container: {
    padding: '20px',
    backgroundColor: '#f9f9f9',
    borderRadius: '8px',
    boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)',
  },
  title: {
    textAlign: 'center' as 'center',
    fontSize: '24px',
    color: '#333',
    marginBottom: '20px',
  },
  dateContainer: {
    display: 'flex',
    justifyContent: 'space-between',
    marginBottom: '20px',
  },
  label: {
    fontSize: '16px',
    color: '#555',
    marginRight: '10px',
  },
  dateInput: {
    padding: '8px',
    borderRadius: '8px',
    border: '1px solid #ccc',
    width: '100%',
    fontSize: '16px',
  },
  loadingText: {
    textAlign: 'center' as 'center',
    fontSize: '18px',
    color: '#555',
  },
  analyticsContainer: {
    marginTop: '20px',
  },
  summaryContainer: {
    marginBottom: '20px',
  },
  productContainer: {
    marginTop: '20px',
  },
  chartContainer: {
    marginTop: '20px',
  },
  imageGallery: {
    display: 'flex',
    gap: '10px',
    marginBottom: '10px',
  },
  productImage: {
    width: '100px',
    height: '100px',
    objectFit: 'cover' as 'cover',
    borderRadius: '8px',
    boxShadow: '0 2px 4px rgba(0, 0, 0, 0.1)',
  },
  noDataText: {
    textAlign: 'center' as 'center',
    fontSize: '18px',
    color: '#888',
  },
};

export default AnalyticsDashboard;
