import React, { useState } from 'react';
import Box from '@mui/material/Box';
import FormControl from '@mui/material/FormControl';
import InputLabel from '@mui/material/InputLabel';
import Select from '@mui/material/Select';
import MenuItem from '@mui/material/MenuItem';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import { addActivity } from '../services/api';

const ActivityForm = ({ onActivitySubmitted }) => {

  const [activity, setActivity] = useState({
    type: "RUNNING",
    duration: '',
    caloriesBurned: '',
    additionalMetrics: {}
  });

  const handleSubmit = async (event) => {
    event.preventDefault();
    try {
      await addActivity({
        ...activity,
        userId: "123", // ✅ add userId (replace with real user later)
        duration: Number(activity.duration), // ✅ convert to number
        caloriesBurned: Number(activity.caloriesBurned), // ✅ convert
        startTime: new Date().toISOString() // ✅ optional but safe
      });

      onActivitySubmitted();

      setActivity({
        type: "RUNNING",
        duration: '',
        caloriesBurned: '',
        additionalMetrics: {}
      });

    } catch (error) {
      console.error(error);
    }
  };

  return (
    <Box component="form" onSubmit={handleSubmit} sx={{ mb: 4 }}>
      
      <FormControl fullWidth sx={{ mb: 2 }}>
        <InputLabel id="activity-type-label">Activity Type</InputLabel>
        <Select
          labelId="activity-type-label"
          value={activity.type}
          onChange={(e) => setActivity({ ...activity, type: e.target.value })}
          label="Activity Type"
        >
          <MenuItem value="RUNNING">Running</MenuItem>
          <MenuItem value="CYCLING">Cycling</MenuItem>
          <MenuItem value="SWIMMING">Swimming</MenuItem>
          <MenuItem value="YOGA">Yoga</MenuItem>

          {/* ✅ FIXED ENUM VALUE */}
          <MenuItem value="WEIGHT_TRAINING">Weight Training</MenuItem>

          <MenuItem value="WALKING">Walking</MenuItem>
        </Select>
      </FormControl>

      <TextField
        label="Duration (minutes)"
        type="number"
        fullWidth
        value={activity.duration}
        onChange={(e) =>
          setActivity({ ...activity, duration: e.target.value })
        }
        sx={{ mb: 2 }}
      />

      <TextField
        label="Calories Burned"
        type="number"
        fullWidth
        value={activity.caloriesBurned}
        onChange={(e) =>
          setActivity({ ...activity, caloriesBurned: e.target.value })
        }
        sx={{ mb: 2 }}
      />

      <Button variant="contained" color="primary" type="submit">
        Add Activity
      </Button>

    </Box>
  );
};

export default ActivityForm;