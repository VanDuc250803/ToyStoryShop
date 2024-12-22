import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom'; // Import useNavigate để điều hướng
import axios from 'axios';

interface ArtStory {
  _id?: string;
  title: string;
  author?: string;
  date?: string;
  description?: string;
  content?: string;
  caption?: string[];
  imageUrl?: string[];
}

const ArtStoryDetail: React.FC = () => {
  const { id } = useParams(); // Lấy id từ URL
  const [artStory, setArtStory] = useState<ArtStory | null>(null);
  const navigate = useNavigate(); // Hook điều hướng

  useEffect(() => {
    const fetchArtStory = async () => {
      try {
        const response = await axios.get(`http://localhost:28017/artstories/${id}`);
        setArtStory(response.data);
      } catch (error) {
        console.error('Lỗi khi tải chi tiết bài viết!', error);
      }
    };
    fetchArtStory();
  }, [id]);

  if (!artStory) return <div>Loading...</div>;

  return (
    <div style={{ padding: '20px', display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
      <div style={{ flex: 1 }}>
        <h1 style={{ textAlign: 'center' }}>{artStory.title || 'Tiêu đề không có'}</h1>
        <h3 style={{ textAlign: 'center', color: '#555' }}>{artStory.author || 'Tác giả chưa có'}</h3>
        <p><strong>Ngày tạo:</strong> {new Date(artStory.date || '').toLocaleDateString()}</p>
        <p><strong>Mô tả:</strong> {artStory.description || 'Không có mô tả'}</p>
        <div>
          <strong>Nội dung:</strong>
          <p>{artStory.content || 'Nội dung bài viết chưa có'}</p>
        </div>
        
        {/* Hiển thị ảnh nếu có */}
        {artStory.imageUrl && artStory.imageUrl.length > 0 && (
          <div style={{ textAlign: 'center', margin: '20px 0' }}>
            <img src={artStory.imageUrl[0]} alt="ArtStory" style={{ width: '80%', height: 'auto', borderRadius: '8px' }} />
          </div>
        )}

        {/* Hiển thị các caption nếu có */}
        {artStory.caption && artStory.caption.length > 0 && (
          <div>
            <strong>Chú thích:</strong>
            <ul>
              {artStory.caption.map((caption, index) => (
                <li key={index}>{caption}</li>
              ))}
            </ul>
          </div>
        )}
      </div>

      {/* Nút quay lại */}
      <div style={{ textAlign: 'center', marginTop: '20px' }}>
        <button
          onClick={() => navigate(-1)} // Quay lại trang trước đó
          style={{
            padding: '10px 20px',
            backgroundColor: '#007bff',
            color: 'white',
            border: 'none',
            borderRadius: '5px',
            cursor: 'pointer',
            fontSize: '16px',
          }}
        >
          Quay lại
        </button>
      </div>
    </div>
  );
};

export default ArtStoryDetail;
