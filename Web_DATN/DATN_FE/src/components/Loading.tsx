import React from 'react'
import {
    LoadingOutlined
} from '@ant-design/icons';

type Props = {
  size?: string; 
}

const LoadingComponent = ({ size = '24px' }: Props) => {
  return (
    <>
      <div className='fixed top-0 right-0 left-0 bottom-0 z-100 bg-[#ffffff9e] flex align-center justify-center'>
        <LoadingOutlined style={{ fontSize: size }} />
      </div>
    </>
  )
}

export default LoadingComponent;