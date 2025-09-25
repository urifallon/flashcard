import React from 'react';
import './App.css';
import ExampleComponent from './components/ExampleComponent';

const App: React.FC = () => {
  return (
    <div className="App">
      <header className="App-header">
        <h1>Welcome to My React Frontend</h1>
      </header>
      <main>
        <ExampleComponent />
      </main>
    </div>
  );
}

export default App;