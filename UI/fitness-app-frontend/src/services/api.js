import axios from 'axios';
const API_BASE_URL = 'http://localhost:9095/api/';

const api= axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json'
    }
});

api.interceptors.request.use(config => {
    const token = localStorage.getItem('token');
    const userId= localStorage.getItem('userId');
    if (token) {
        config.headers['Authorization'] = `Bearer ${token}`;
    }   
    if(userId)
    {
        config.headers['X-User-Id']=userId;
    }
    return config;
}, error => {
    return Promise.reject(error);
});

export  const getActivities = async () => {
    const response = await api.get('/activities');
    return response.data;
}

export  const addActivity = async (activityData) => {
    const response = await api.post('/activities/create', activityData);
    return response.data;
}

export  const getActivityDetails = async (id) => {
    const response = await api.get(`/recommendations/activity/${id}` );
    return response.data;
}
