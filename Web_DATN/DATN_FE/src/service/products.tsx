import React from 'react'
import { axiosservice } from '../config/API'
import { IProductLite } from '../interface/products';

export const getAllProducts = async () => {
  try{
    const {data} = await axiosservice.get('product')
    return data    
  } catch (error) {
    console.log(error);
  }
}
export const getProductDetails = async (id?:string) => {
  try {
    const { data } = await axiosservice.get(`/product/details/${id}`);
    return data;
  } catch (error) {
    console.log(error);
  }
};

export const getProductByID = async(id?:string) =>{
  try {
    const {data} = await axiosservice.get(`/product/${id}`)
    return data
  } catch (error){
    console.log(error);
    
  }
}

export const hideProduct = async (id: string) => {
  try {
    const { data } = await axiosservice.put(`/product/hide/${id}`, { isHidden: true });
    return data;
  } catch (error) {
    console.log(error);
  }
};

export const addProduct = async(product:IProductLite) => {
    try {
        const { data } = await axiosservice.post('product/add', product)
        return data
} catch (error) {
    console.log(error);
    
}
}

export const updateProduct = async(id?:string,   product?: IProductLite) => {
  try {
    const {data} = await axiosservice.put(`/update/${id}`, product)
    return data
  } catch (error) {
    console.log(error);
    
  }
}

export const DeleteProduct = async(pid:string)=> {
  try{
    const{data} = await axiosservice.delete(`/product/${pid}`)
    return data
  } catch (error){
    console.log(error);
    
  }
}
