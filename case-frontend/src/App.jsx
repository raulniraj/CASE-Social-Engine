import { useState, useEffect } from 'react';
import axios from 'axios';
import { PieChart, Pie, Cell, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import './App.css';

function App() {
  const [token, setToken] = useState(localStorage.getItem('jwt') || '');
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');

  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [content, setContent] = useState('');
  const [platform, setPlatform] = useState('TWITTER');
  const [scheduledTime, setScheduledTime] = useState('');
  const [enableRule, setEnableRule] = useState(false);
  const [ruleType, setRuleType] = useState('WEATHER');
  const [conditionValue, setConditionValue] = useState('RAIN');
  const [ruleAction, setRuleAction] = useState('BLOCK_POST');

  useEffect(() => {
    if (token) {
      fetchPosts();
    }
  }, [token]);

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post('http://localhost:8080/api/v1/auth/login', {
        username: username,
        password: password
      });
      const jwt = response.data.token;
      setToken(jwt);
      localStorage.setItem('jwt', jwt);
    } catch (error) {
      console.error("Login failed:", error);
      alert("Invalid credentials. Try again.");
    }
  };

  const handleLogout = () => {
    setToken('');
    localStorage.removeItem('jwt');
    setPosts([]);
  };

  const fetchPosts = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/v1/posts/user/1', {
        headers: { Authorization: `Bearer ${token}` }
      });
      const sortedPosts = response.data.sort((a, b) => b.id - a.id);
      setPosts(sortedPosts);
      setLoading(false);
    } catch (error) {
      console.error("Error fetching posts:", error);
      setLoading(false);
    }
  };

 const handleSubmit = async (e) => {
  e.preventDefault();

  const newPost = {
    userId: 1,
    content: content,
    platform: platform,
    scheduledTime: scheduledTime.length === 16 ? `${scheduledTime}:00` : scheduledTime,
    rules: enableRule ? [{ ruleType, conditionValue, action: ruleAction }] : []
  };

  try {
    // 1. We MUST pass the token here
    const config = {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json' // This is the secret sauce!
      }
    };

    // 2. Add 'config' as the 3rd parameter
    await axios.post('http://localhost:8080/api/v1/posts', newPost, config);
    
    setContent('');
    setScheduledTime('');
    setEnableRule(false);
    fetchPosts(); // Refresh the stream
    alert("Post Scheduled Successfully!");
  } catch (error) {
    console.error("Error creating post:", error);
    alert("Failed to schedule post. Try logging out and back in.");
  }
};

  const getStatusBadge = (status) => {
    switch (status) {
      case 'PUBLISHED': return <span className="badge published">✅ Published</span>;
      case 'PENDING': return <span className="badge pending">⏳ Pending</span>;
      case 'BLOCKED': return <span className="badge blocked">🛑 Blocked by AI</span>;
      default: return <span className="badge">{status}</span>;
    }
  };

  // --- Analytics Data Calculation ---
  const publishedCount = posts.filter(p => p.status === 'PUBLISHED').length;
  const pendingCount = posts.filter(p => p.status === 'PENDING').length;
  const blockedCount = posts.filter(p => p.status === 'BLOCKED').length;

  const chartData = [
    { name: 'Published', value: publishedCount, color: '#28a745' },
    { name: 'Pending', value: pendingCount, color: '#ffc107' },
    { name: 'Blocked', value: blockedCount, color: '#dc3545' }
  ].filter(item => item.value > 0); // Hide empty slices

  // --- RENDER: Login Screen ---
  if (!token) {
    return (
      <div className="login-wrapper">
        <div className="login-card">
          <div className="login-header">
            <h2>🛡️ System Access</h2>
            <p>Enter your credentials to access the engine.</p>
          </div>
          <form onSubmit={handleLogin} autoComplete="off">
            <div className="form-group">
              <label>Username</label>
              <input type="text" value={username} onChange={(e) => setUsername(e.target.value)} required autoComplete="off" />
            </div>
            <div className="form-group">
              <label>Password</label>
              <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required autoComplete="new-password" />
            </div>
            <button type="submit" className="login-btn">Authenticate</button>
          </form>
        </div>
      </div>
    );
  }

  // --- RENDER: Main Dashboard ---
  return (
    <div className="dashboard-container">
      <header className="dashboard-header">
        <h1>⚙️ CASE Command Center</h1>
        <div className="header-actions">
          <button onClick={fetchPosts} className="refresh-btn">🔄 Refresh</button>
          <button onClick={handleLogout} className="logout-btn">🚪 Logout</button>
        </div>
      </header>

      {/* --- NEW: Analytics Row --- */}
      <div className="analytics-row">
        <div className="card stat-card">
          <h3>Total Posts</h3>
          <div className="stat-number">{posts.length}</div>
        </div>
        <div className="card chart-card">
          <h3>Engine Status Distribution</h3>
          <div className="chart-container">
            {chartData.length > 0 ? (
              <ResponsiveContainer width="100%" height={200}>
                <PieChart>
                  <Pie data={chartData} innerRadius={60} outerRadius={80} paddingAngle={5} dataKey="value">
                    {chartData.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={entry.color} />
                    ))}
                  </Pie>
                  <Tooltip />
                  <Legend verticalAlign="middle" align="right" layout="vertical" />
                </PieChart>
              </ResponsiveContainer>
            ) : (
              <p className="no-data">No data to display yet.</p>
            )}
          </div>
        </div>
      </div>

      <div className="dashboard-grid">
        <div className="card form-card">
          <h2>Create New Post</h2>
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label>Content:</label>
              <textarea value={content} onChange={(e) => setContent(e.target.value)} required />
            </div>

            <div className="form-row">
              <div className="form-group">
                <label>Platform:</label>
                <select value={platform} onChange={(e) => setPlatform(e.target.value)}>
                  <option value="TWITTER">Twitter</option>
                  <option value="LINKEDIN">LinkedIn</option>
                  <option value="INSTAGRAM">Instagram</option>
                </select>
              </div>
              <div className="form-group">
                <label>Time:</label>
                <input type="datetime-local" value={scheduledTime} onChange={(e) => setScheduledTime(e.target.value)} required />
              </div>
            </div>

            <div className="rule-toggle-container">
              <label className="toggle-label">
                <input type="checkbox" checked={enableRule} onChange={(e) => setEnableRule(e.target.checked)} />
                Attach AI Context Rule
              </label>
            </div>

            {enableRule && (
              <div className="rule-builder">
                <h4>Drools AI Rule</h4>
                <div className="form-row">
                  <select value={ruleType} onChange={(e) => setRuleType(e.target.value)}>
                    <option value="WEATHER">Weather</option>
                    <option value="NEWS">News</option>
                  </select>
                  <select value={conditionValue} onChange={(e) => setConditionValue(e.target.value)}>
                    <option value="RAIN">If Rain</option>
                    <option value="CLOUDS">If Clouds</option>
                    <option value="CLEAR">If Clear</option>
                    <option value="DISASTER">If Emergency</option>
                  </select>
                  <select value={ruleAction} onChange={(e) => setRuleAction(e.target.value)}>
                    <option value="BLOCK_POST">Halt Post</option>
                  </select>
                </div>
              </div>
            )}
            <button type="submit" className="submit-btn">🚀 Schedule Post</button>
          </form>
        </div>

{/* --- Main Dashboard Body --- */}
<div className="hootsuite-streams">
  
  {/* Stream 1: Twitter */}
  <div className="stream-column">
    <div className="stream-header">
      <img src="https://abs.twimg.com/favicons/twitter.2.ico" alt="X" />
      <h3>Twitter / X</h3>
    </div>
    <div className="stream-content">
      {posts.filter(p => p.platform === 'TWITTER').map(post => (
        <div key={post.id} className="post-card">
          <p>{post.content}</p>
          <div className="post-footer">{getStatusBadge(post.status)}</div>
        </div>
      ))}
    </div>
  </div>

  {/* Stream 2: LinkedIn */}
  <div className="stream-column">
    <div className="stream-header">
      <img src="https://static.licdn.com/sc/h/al2o9zrvru7aqj8e1x2rzsrca" alt="LI" />
      <h3>LinkedIn</h3>
    </div>
    <div className="stream-content">
      {posts.filter(p => p.platform === 'LINKEDIN').map(post => (
        <div key={post.id} className="post-card">
          <p>{post.content}</p>
          <div className="post-footer">{getStatusBadge(post.status)}</div>
        </div>
      ))}
    </div>
  </div>

  {/* Stream 3: Instagram */}
  <div className="stream-column">
    <div className="stream-header">
      <img src="https://www.instagram.com/static/images/ico/favicon.ico/36b304837401.ico" alt="IG" />
      <h3>Instagram</h3>
    </div>
    <div className="stream-content">
      {posts.filter(p => p.platform === 'INSTAGRAM').map(post => (
        <div key={post.id} className="post-card">
          <p>{post.content}</p>
          <div className="post-footer">{getStatusBadge(post.status)}</div>
        </div>
      ))}
    </div>
  </div>

</div>
          
        </div>
      </div>
    
  );
}

export default App;