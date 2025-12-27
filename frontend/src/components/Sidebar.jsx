import { LayoutDashboard, Receipt, Activity, Settings } from 'lucide-react';

const Sidebar = ({ activeParams, onNavigate }) => {
    const menuItems = [
        { id: 'dashboard', label: 'Dashboard', icon: LayoutDashboard },
        { id: 'transactions', label: 'Transactions', icon: Receipt },
        { id: 'activity', label: 'Activity Log', icon: Activity },
        { id: 'settings', label: 'Settings', icon: Settings },
    ];

    return (
        <div className="h-screen w-64 bg-slate-900 text-white flex flex-col p-4">
            <div className="mb-8 flex items-center gap-2">
                <div className="w-8 h-8 bg-blue-500 rounded-lg flex items-center justify-center font-bold">LS</div>
                <span className="text-xl font-bold tracking-tight">LedgerSync</span>
            </div>

            <nav className="flex-1 space-y-1">
                {menuItems.map((item) => {
                    const Icon = item.icon;
                    const isActive = activeParams === item.id;
                    return (
                        <button
                            key={item.id}
                            onClick={() => onNavigate(item.id)}
                            className={`w-full flex items-center gap-3 px-3 py-2 rounded-md transition-colors ${isActive ? 'bg-blue-600 text-white' : 'text-slate-400 hover:bg-slate-800 hover:text-white'
                                }`}
                        >
                            <Icon size={20} />
                            <span className="font-medium">{item.label}</span>
                        </button>
                    );
                })}
            </nav>

            <div className="text-xs text-slate-500 mt-auto pt-4 border-t border-slate-800">
                v1.0.0 (Beta)
            </div>
        </div>
    );
};

export default Sidebar;
