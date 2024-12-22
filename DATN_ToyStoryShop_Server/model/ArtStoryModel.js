// models/ArtStoryModel.js
const mongoose = require('mongoose');

const artStorySchema = new mongoose.Schema({
    title: { type: String, required: true },
    author: String,
    date: { type: Date, default: Date.now },
    description: String,
    content: String,
    caption: [{type:String}],
    imageUrl: [{type:String}]   // Trường URL của ảnh
});

module.exports = mongoose.model('ArtStory', artStorySchema);
