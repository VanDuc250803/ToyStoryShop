import React, { useState, useEffect } from 'react';
import axios from 'axios';

interface Message {
  _id: string;
  cusId: string;
  userId: string;
  message: string;
  chatType: string;
  timestamp: string;
  chatStatus: string;
  replyTo?: string;  // Thêm trường này để xác định tin nhắn trả lời
}

const Chat: React.FC = () => {
  const [messages, setMessages] = useState<Message[]>([]);
  const [newMessage, setNewMessage] = useState<string>('');
  const [currentCusId, setCurrentCusId] = useState<string>('');
  const [selectedId, setSelectedId] = useState<string>('');
  const [currentUserId] = useState<string>('admin');
  const [replyToMessageId, setReplyToMessageId] = useState<string | null>(null); // Lưu id tin nhắn đang trả lời

  useEffect(() => {
    const fetchMessages = async () => {
      try {
        const response = await axios.get('http://localhost:28017/chats');
        if (Array.isArray(response.data)) {
          setMessages(response.data);
        } else if (response.data && typeof response.data === 'object') {
          setMessages([response.data]);
        } else {
          console.error('Dữ liệu trả về không hợp lệ:', response.data);
        }
      } catch (error) {
        console.error('Lỗi khi lấy danh sách tin nhắn:', error);
      }
    };
    fetchMessages();
  }, []);

  const fetchMessagesByCusId = async (cusId: string) => {
    try {
      const response = await axios.get(`http://localhost:28017/messages/${cusId}`);
      if (Array.isArray(response.data)) {
        setMessages(response.data);
      } else {
        console.error('Dữ liệu trả về không hợp lệ:', response.data);
      }
    } catch (error) {
      console.error('Lỗi khi lấy tin nhắn theo cusId:', error);
    }
  };

  const sendMessage = async () => {
    if (!newMessage || !currentCusId || !currentUserId) return;
  
    const messageData = {
      cusId: currentCusId,
      userId: currentUserId,
      message: newMessage,
      chatType: 'Văn bản',
      chatStatus: 'Đã gửi',
      replyTo: replyToMessageId ? replyToMessageId : undefined,  // Đảm bảo replyTo là undefined nếu không có giá trị
    };
  
    try {
      const response = await axios.post('http://localhost:28017/messages', messageData);
      setNewMessage('');
      
      // Đảm bảo tin nhắn mới thêm vào tuân thủ đúng cấu trúc kiểu
      setMessages((prev) => [
        ...prev,
        { 
          ...messageData, 
          _id: response.data._id || new Date().toISOString(), 
          timestamp: new Date().toISOString() 
        }
      ]);
  
      setReplyToMessageId(null); // Reset lại replyToMessageId sau khi gửi
    } catch (error: any) {
      console.error('Lỗi khi gửi tin nhắn:', error.response?.data || error.message);
    }
  };
  

  const handleCustomerClick = (id: string) => {
    setSelectedId(id);
    fetchMessagesByCusId(id);
    setCurrentCusId(id);
  };

  const handleLongPress = async (id: string) => {
    if (window.confirm('Bạn có chắc chắn muốn xóa cuộc trò chuyện này?')) {
      try {
        await axios.delete(`http://localhost:28017/messages/${id}`);
        setMessages((prev) => prev.filter((msg) => msg._id !== id));
      } catch (error) {
        console.error('Lỗi khi xóa cuộc trò chuyện:', error);
      }
    }
  };

  // Hàm để định dạng ngày giờ
  const formatTimestamp = (timestamp: string) => {
    const date = new Date(timestamp);
    const day = date.getDate().toString().padStart(2, '0');
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const year = date.getFullYear();
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    return `${day}/${month}/${year} ${hours}:${minutes}`;
  };

  return (
    <div style={{ display: 'flex', height: '100vh', fontFamily: 'Arial, sans-serif' }}>
      <div style={{ width: '25%', borderRight: '1px solid #ccc', padding: '16px', overflowY: 'auto' }}>
        <h2 style={{ textAlign: 'center', color: '#007bff', borderBottom: '2px solid #007bff', paddingBottom: '8px' }}>
          Danh sách tin nhắn
        </h2>
        {messages.map((msg) => (
          <div
            key={msg._id}
            onClick={() => handleCustomerClick(msg.cusId)}
            onContextMenu={(e) => {
              e.preventDefault();
              handleLongPress(msg._id);
            }}
            style={{
              padding: '8px',
              margin: '8px 0',
              cursor: 'pointer',
              backgroundColor: selectedId === msg._id ? '#007bff' : '#f9f9f9',
              color: selectedId === msg._id ? 'white' : 'black',
              borderRadius: '4px',
              textAlign: 'center',
            }}
          >
            {msg.message} <br />
            <span style={{ fontSize: '0.9em', color: '#777' }}>Khách hàng ID: {msg.cusId}</span>
          </div>
        ))}
      </div>
      <div style={{ flex: '1', display: 'flex', flexDirection: 'column', padding: '16px' }}>
        <h2 style={{ textAlign: 'center', color: '#007bff', borderBottom: '2px solid #007bff', paddingBottom: '8px' }}>
          Chăm sóc khách hàng
        </h2>
        <div style={{ flex: '1', overflowY: 'auto', marginBottom: '16px', border: '1px solid #ccc', padding: '8px' }}>
          {messages
            .filter((msg) => msg.cusId === currentCusId)
            .map((msg) => (
              <div
                key={msg._id}
                style={{
                  marginBottom: '12px',
                  padding: '8px',
                  border: '1px solid #f0f0f0',
                  borderRadius: '4px',
                  backgroundColor: msg.userId === 'admin' ? '#007bff' : '#f1f1f1',
                  color: msg.userId === 'admin' ? 'white' : 'black',
                  maxWidth: '50%',
                  wordWrap: 'break-word',
                  alignSelf: msg.userId === 'admin' ? 'flex-end' : 'flex-start',
                  marginLeft: msg.userId === 'admin' ? 'auto' : 'initial',
                  marginRight: msg.userId === 'admin' ? '0' : 'initial',
                }}
              >
                <strong>{msg.userId}:</strong> {msg.message}
                <div
                  style={{
                    fontSize: '0.8em',
                    color: msg.userId === 'admin' ? 'white' : '#777',
                    marginTop: '4px',
                    textAlign: 'right',
                  }}
                >
                  {formatTimestamp(msg.timestamp)}
                </div>
                {/* Hiển thị tin nhắn trả lời nếu có */}
                {msg.replyTo && (
                  <div style={{ marginTop: '8px', padding: '6px', borderLeft: '2px solid #007bff', backgroundColor: '#f1f1f1' }}>
                    <strong>Trả lời:</strong> {msg.message}
                  </div>
                )}
              </div>
            ))}
        </div>
        <div style={{ display: 'flex', alignItems: 'center' }}>
          <textarea
            placeholder="Nhập tin nhắn..."
            value={newMessage}
            onChange={(e) => setNewMessage(e.target.value)}
            style={{
              flex: '1',
              marginRight: '8px',
              padding: '8px',
              borderRadius: '4px',
              border: '1px solid #ccc',
              height: '50px',
            }}
          ></textarea>
          <button
            onClick={sendMessage}
            style={{
              backgroundColor: '#28a745',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              padding: '8px 16px',
              cursor: 'pointer',
            }}
          >
            Gửi
          </button>
        </div>
      </div>
    </div>
  );
};

export default Chat;
