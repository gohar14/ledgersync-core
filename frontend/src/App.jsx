import { useState } from 'react';
import Sidebar from './components/Sidebar';
import DashboardView from './views/DashboardView';
import TransactionTable from './components/TransactionTable';

function App() {
  const [activeView, setActiveView] = useState('dashboard');

  return (
    <div className="flex min-h-screen bg-slate-50 font-sans">
      <Sidebar activeParams={activeView} onNavigate={setActiveView} />

      <main className="flex-1 p-8 overflow-y-auto h-screen">
        <div className="max-w-7xl mx-auto">
          {activeView === 'dashboard' && <DashboardView />}
          {activeView === 'transactions' && <TransactionTable />}
          {activeView === 'activity' && <div className="p-8 text-center text-slate-500">Activity Log Coming Soon</div>}
          {activeView === 'settings' && <div className="p-8 text-center text-slate-500">Settings Coming Soon</div>}
        </div>
      </main>
    </div>
  );
}

export default App;
